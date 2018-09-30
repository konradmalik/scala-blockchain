package io.github.konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, ActorRef, DeadLetter, Props, Terminated}
import io.github.konradmalik.blockchain._
import io.github.konradmalik.blockchain.api._
import io.github.konradmalik.blockchain.api.actors.Supervisor._
import io.github.konradmalik.blockchain.api.routes.Success

object Supervisor {
  def props() = Props(new Supervisor)

  sealed trait Initialization

  final case class InitializeBlockchain(timestamp: Long) extends Initialization

  final case class InitializedBlockchain(timestamp: Long, actor: ActorRef) extends Success

  final case class InitializePeer(timestamp: Long) extends Initialization

  final case class InitializedPeer(timestamp: Long, actor: ActorRef) extends Success

  final case class InitializeBlockPool(timestamp: Long) extends Initialization

  final case class InitializedBlockPool(timestamp: Long, actor: ActorRef) extends Success

}

class Supervisor extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = {
    case InitializeBlockchain(rId) => initializeBlockchain(rId)

    case InitializePeer(rId) => initializePeer(rId)

    case InitializeBlockPool(rId) => initializeBlockPool(rId)

    case Terminated(actor) =>
      log.info("Network {} has been terminated", actor)

    case d: DeadLetter =>
      log.error(s"{} saw dead letter $d", this.getClass.getSimpleName)

    case _ => log.info("Unknown message sent to the {} by {}", this.getClass.getSimpleName, sender())
  }

  private def initializeBlockchain(requestId: Long): Unit = {
    val actor = context.actorOf(BlockchainActor.props(DIFFICULTY), BLOCKCHAIN_ACTOR_NAME)
    log.info("Created Blockchain, name {}", BLOCKCHAIN_ACTOR_NAME)
    context.watch(actor)
    sender() ! InitializedBlockchain(requestId, actor)
  }

  private def initializePeer(requestId: Long): Unit = {
    val actor = context.actorOf(PeerActor.props, PEER_ACTOR_NAME)
    log.info("Created Peer, name {}", PEER_ACTOR_NAME)
    context.watch(actor)
    sender() ! InitializedPeer(requestId, actor)
  }

  private def initializeBlockPool(requestId: Long): Unit = {
    val actor = context.actorOf(BlockPoolActor.props, BLOCK_POOL_ACTOR_NAME)
    log.info("Created Peer, name {}", BLOCK_POOL_ACTOR_NAME)
    context.watch(actor)
    sender() ! InitializedBlockPool(requestId, actor)
  }
}

