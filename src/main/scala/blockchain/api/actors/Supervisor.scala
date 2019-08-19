package blockchain.api.actors

import akka.actor.{Actor, ActorLogging, ActorRef, DeadLetter, Props, Terminated}
import blockchain.api.actors.Supervisor._

object Supervisor {
  def props = Props(new Supervisor)

  final case object InitializeBlockchain

  final case class InitializedBlockchain(actor: ActorRef)

  final case object InitializePeer

  final case class InitializedPeer(actor: ActorRef)

  final case object InitializeBlockPool

  final case class InitializedBlockPool(actor: ActorRef)

}

class Supervisor extends Actor with ActorLogging {

  import blockchain._
  import blockchain.api._

  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = {
    case InitializeBlockchain => initializeBlockchain()

    case InitializePeer => initializePeer()

    case InitializeBlockPool => initializeBlockPool()

    case Terminated(actor) =>
      log.info("Network {} has been terminated", actor)

    case d: DeadLetter =>
      log.error(s"{} saw dead letter $d", this.getClass.getSimpleName)

    case _ => log.info("Unknown message sent to the {} by {}", this.getClass.getSimpleName, sender())
  }

  private def initializeBlockchain(): Unit = {
    val actor = context.actorOf(BlockchainActor.props(DIFFICULTY), BLOCKCHAIN_ACTOR_NAME)
    log.info("Created Blockchain, name {}", BLOCKCHAIN_ACTOR_NAME)
    context.watch(actor)
    sender() ! InitializedBlockchain(actor)
  }

  private def initializePeer(): Unit = {
    val actor = context.actorOf(PeerActor.props, PEER_ACTOR_NAME)
    log.info("Created Peer, name {}", PEER_ACTOR_NAME)
    context.watch(actor)
    sender() ! InitializedPeer(actor)
  }

  private def initializeBlockPool(): Unit = {
    val actor = context.actorOf(BlockPoolActor.props, BLOCK_POOL_ACTOR_NAME)
    log.info("Created Peer, name {}", BLOCK_POOL_ACTOR_NAME)
    context.watch(actor)
    sender() ! InitializedBlockPool(actor)
  }
}

