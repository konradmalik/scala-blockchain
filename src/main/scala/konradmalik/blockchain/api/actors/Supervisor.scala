package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import konradmalik.blockchain.api._
import konradmalik.blockchain.api.actors.Supervisor._

object Supervisor {
  def props() = Props(new Supervisor)

  sealed trait Initialization

  final case class InitializeBlockchainNetwork(requestId: Long, initialNo: Int) extends Initialization

  final case class InitializePeerNetwork(requestId: Long, initialNo: Int) extends Initialization

  final case class InitializeBlockPoolNetwork(requestId: Long, initialNo: Int) extends Initialization

  final case class InitializedBlockchainNetwork(requestId: Long) extends Initialization

  final case class InitializedPeerNetwork(requestId: Long) extends Initialization

  final case class InitializedBlockPoolNetwork(requestId: Long) extends Initialization

}

class Supervisor extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = {
    case InitializeBlockchainNetwork(rId, iNo) => initializeBlockchainNetwork(rId, iNo)

    case InitializePeerNetwork(rId, iNo) => initializePeerNetwork(rId, iNo)

    case InitializeBlockPoolNetwork(rId, iNo) => initializeBlockPool(rId, iNo)

    case Terminated(actor) ⇒
      log.info("Network {} has been terminated", actor)

    case _ => log.info("Unknown message sent to the {} by {}", this.getClass.getSimpleName, sender())
  }

  private def initializeBlockchainNetwork(requestId: Long, initialNo: Int): Unit = {
    val network = context.actorOf(BlockchainNetwork.props(initialNo), BLOCKCHAIN_NETWORK_ACTOR_NAME)
    context.watch(network)
    sender() ! InitializedBlockchainNetwork(requestId)
  }

  private def initializePeerNetwork(requestId: Long, initialNo: Int): Unit = {
    val network = context.actorOf(PeerNetwork.props(initialNo), PEER_NETWORK_ACTOR_NAME)
    context.watch(network)
    sender() ! InitializedPeerNetwork(requestId)
  }

  private def initializeBlockPool(requestId: Long, initialNo: Int): Unit = {
    val network = context.actorOf(BlockPoolNetwork.props(initialNo), BLOCK_POOL_NETWORK_ACTOR_NAME)
    context.watch(network)
    sender() ! InitializedBlockPoolNetwork(requestId)
  }
}

