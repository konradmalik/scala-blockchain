package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}

object PeerNetwork {
  def props(initialBlockPools: Int) = Props(new PeerNetwork(initialBlockPools))
}

class PeerNetwork(initialBlockPools: Int) extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = Actor.emptyBehavior
}
