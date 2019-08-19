package blockchain.api.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Address, AddressFromURIString, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}
import akka.pattern.ask
import blockchain.Chain
import blockchain.api.actors.BlockchainClusterListener.{GetNodes, RefreshChain}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object BlockchainClusterListener {
  def props(nodeAddress: Option[String]) = Props(new BlockchainClusterListener(nodeAddress))

  def props(nodeAddress: String) = Props(new BlockchainClusterListener(Some(nodeAddress)))

  def props = Props(new BlockchainClusterListener(None))

  final case object GetNodes

  final case object RefreshChain

}

class BlockchainClusterListener(nodeAddress: Option[String]) extends Actor with ActorLogging {

  import blockchain.api._

  var nodes = Set.empty[Address]

  val cluster: Cluster = Cluster(context.system)
  // if existing node provided, then join it, else join itself (important!)
  nodeAddress match {
    case Some(addr) =>
      val address = AddressFromURIString(addr)
      cluster.join(address)
      log.info(s"joined remote cluster: $address")
    case None =>
      cluster.join(cluster.selfAddress)
      log.info(s"joined self cluster: ${cluster.selfAddress}")
  }

  import scala.concurrent.duration._

  context.system.scheduler.schedule(0 seconds, 5 seconds, new Runnable {
    override def run(): Unit = log.info(s"current nodes in the cluster: ${nodes.mkString(";")}")
  })


  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive: PartialFunction[Any, Unit] = {

    case state: CurrentClusterState =>
      nodes = state.members.collect {
        case m if m.status == MemberStatus.Up => m.address
      }
    case MemberUp(member) =>
      nodes += member.address
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      nodes -= member.address
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case GetNodes => sender ! nodes
    case RefreshChain => sender ! refreshChain
    case _: MemberEvent => log.info("Unknown member event ignored")
  }

  private def markFutureToPrintException(f: Future[_]) = f.recover { case e: Exception => e.printStackTrace() }

  def refreshChain: Int = {
    val setOfFutures: Set[Future[ActorRef]] = nodes.map(a => context.actorSelection(s"${a.toString}/user/$SUPERVISOR_ACTOR_NAME/$BLOCKCHAIN_ACTOR_NAME").resolveOne())
    setOfFutures.foreach(markFutureToPrintException)
    val futureSetOfBlockchainNodes: Future[Iterable[ActorRef]] = futuresToFuture(setOfFutures)
    // send getChain to each node
    markFutureToPrintException(futureSetOfBlockchainNodes)
    val futureSetOfChainLengths: Future[Iterable[(ActorRef, Int)]] = futureSetOfBlockchainNodes
      .map(_.map(ar => (ar ? BlockchainActor.GetLength).mapTo[Int].map(int => ar -> int)))
      .flatMap(futuresToFuture)
    markFutureToPrintException(futureSetOfChainLengths)
    val futureLongestActor: Future[(ActorRef, Int)] = futureSetOfChainLengths.map(_.head)
    markFutureToPrintException(futureLongestActor)
    if (nodes.size == 1) {
      Await.result(futureLongestActor, selectionTimeout)._2
    } else {
      // get chain from longest actor
      val futureLongestChain: Future[Chain] = futureLongestActor
        .flatMap(p => (p._1 ? BlockchainActor.GetChain).mapTo[Chain])
      markFutureToPrintException(futureLongestChain)
      val futureLongestChainLength: Future[Int] = futureLongestActor.map(_._2)
      // replace chain in shorter actors
      // but we must wait for length and chain here
      markFutureToPrintException(futureLongestChainLength)
      val futureActorsToReplace: Future[Iterable[ActorRef]] = for {
        longestActor <- futureLongestActor
        setOfChainLengths <- futureSetOfChainLengths
        longestChainLength <- futureLongestChainLength
      } yield {
        setOfChainLengths.filter(p => p._2 <= longestChainLength && p._1.compareTo(longestActor._1) != 0)
          .map(_._1)
      }
      markFutureToPrintException(futureActorsToReplace)
      // await actors to replace and send replace chain
      val futureReplacementResponses: Future[Iterable[(ActorRef, Boolean)]] = (for {
        actors <- futureActorsToReplace
        replacementChain <- futureLongestChain
      } yield {
        Future.sequence(actors.map(actor =>
          (actor ? BlockchainActor.ReplaceChain(replacementChain)).mapTo[Boolean].map(cv => actor -> cv)))
      }).flatMap(identity)
      markFutureToPrintException(futureReplacementResponses)
      // based on responses, filter out those that replied false from nodes (can we actually kick them out?)
      val futureFailedUpdates: Future[Iterable[(ActorRef, Boolean)]] = for {
        longestActor <- futureLongestActor
        replacementResponses <- futureReplacementResponses
      } yield {
        replacementResponses.filterNot(p => p._2 || p._1.compareTo(longestActor._1) == 0)
      }
      markFutureToPrintException(futureFailedUpdates)
      // now we can kick out nodes which failed
      futureFailedUpdates.map(_.foreach(p => {
        cluster.leave(p._1.path.address)
      }))
      Await.result(futureLongestChainLength, selectionTimeout)
    }
  }

  private def futuresToFuture[T](futures: Iterable[Future[T]]): Future[Iterable[T]] = {
    Future.sequence(futures.map(futureToFutureTry)).map(_.collect { case Success(x) => x })
  }

  private def futureToFutureTry[T](f: Future[T]): Future[Try[T]] = f.map(Success(_)).recover({ case e => Failure(e) })
}
