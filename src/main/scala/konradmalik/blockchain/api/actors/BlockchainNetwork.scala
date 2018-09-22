package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.pattern.ask
import konradmalik.blockchain.api.actors.BlockchainNetwork._
import konradmalik.blockchain.api.{BLOCKCHAIN_ACTOR_NAME, DIFFICULTY, _}

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object BlockchainNetwork {
  def props(initialBlockchains: Int) = Props(new BlockchainNetwork(initialBlockchains))

  final case class GetLongestChain(requestId: Int)

  final case class GetAllChains(requestId: Int)

  final case class GetChainById(requestId: Int, chainId: String)

  final case class AllChains(requestId: Long, blockchainIdToActor: Map[String, ActorRef])

  final case class Chain(requestId: Long, blockchain: ActorRef)

  final case class ChainNotFound(requestId: Long, chainId: String)

}

class BlockchainNetwork(initialBlockchains: Int) extends Actor with ActorLogging {
  // for use with Futures, Scheduler, etc.
  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  val blockchainIdToActor: MutableMap[String, ActorRef] = MutableMap.empty[String, ActorRef]
  // initialize specified number of blockchains
  (0 until initialBlockchains).foreach(id => initializeBlockchain(id.toString))

  var activeChain: ActorRef = _

  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = {
    case GetAllChains(rId) => sender() ! AllChains(rId, blockchainIdToActor.toMap)

    case GetChainById(rId, chId) =>
      if (blockchainIdToActor.contains(chId))
        sender() ! Chain(rId, blockchainIdToActor(chId))
      else
        sender() ! ChainNotFound(rId, chId)

    case GetLongestChain(rId) =>
      if (blockchainIdToActor.isEmpty) sender() ! ChainNotFound(rId, "")
      else sender() ! getLongestChain(rId)

    case makeBlockMsg@BlockchainActor.MakeNewBlock(_,_) => activeChain forward makeBlockMsg

    case Terminated(actor) ⇒
      log.info("Blockchain {} has been terminated", actor)

    case _ => log.info("Unknown message sent to the {} by {}", this.getClass.getSimpleName, sender())
  }

  private def initializeBlockchain(id: String): Unit = {
    val actor = context.actorOf(BlockchainActor.props(id, DIFFICULTY), BLOCKCHAIN_ACTOR_NAME + "-" + id)
    log.info("Created Blockchain, name {}", BLOCKCHAIN_ACTOR_NAME + "-" + id)
    context.watch(actor)
    blockchainIdToActor.update(id, actor)
  }

  private def getLongestChain(requestId: Long): Chain = {
    def futureToFutureOption[T](f: Future[T]): Future[Option[T]] =
      f.map(Some(_)).recover {
        case _ => None
      }

    val listOfFutureOptions = blockchainIdToActor.map {
      case (_, ref) => (ref ? BlockchainActor.GetLength(0)).map(response => ref -> response.asInstanceOf[BlockchainActor.ChainLength])
    }.map(futureToFutureOption)

    val futureListOfOptions = Future.sequence(listOfFutureOptions)

    val futureListOfSuccesses = futureListOfOptions.map(_.flatten)

    Await.result(futureListOfSuccesses.map(it => {
      val maxChain  = it.maxBy(_._2.chainLength)._1
      activeChain = maxChain
      Chain(requestId, maxChain)
    }), timeout.duration)
  }
}
