package io.github.konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, DeadLetter, Props, Terminated}
import io.github.konradmalik.blockchain._
import io.github.konradmalik.blockchain.api._
import io.github.konradmalik.blockchain.api.actors.Supervisor._
import io.github.konradmalik.blockchain.api.routes.Success

object Supervisor {
  def props() = Props(new Supervisor)

  sealed trait Initialization

  final case class InitializeBlockchain(requestId: Long) extends Initialization

  final case class InitializedBlockchain(requestId: Long) extends Success

  final case class InitializePeer(requestId: Long) extends Initialization

  final case class InitializedPeer(requestId: Long) extends Success

  final case class InitializeBlockPool(requestId: Long) extends Initialization

  final case class InitializedBlockPool(requestId: Long) extends Success

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
    sender() ! InitializedBlockchain(requestId)
  }

  private def initializePeer(requestId: Long): Unit = {
    val actor = context.actorOf(PeerActor.props, PEER_ACTOR_NAME)
    log.info("Created Peer, name {}", PEER_ACTOR_NAME)
    context.watch(actor)
    sender() ! InitializedPeer(requestId)
  }

  private def initializeBlockPool(requestId: Long): Unit = {
    val actor = context.actorOf(BlockPoolActor.props, BLOCK_POOL_ACTOR_NAME)
    log.info("Created Peer, name {}", BLOCK_POOL_ACTOR_NAME)
    context.watch(actor)
    sender() ! InitializedBlockPool(requestId)
  }
}

