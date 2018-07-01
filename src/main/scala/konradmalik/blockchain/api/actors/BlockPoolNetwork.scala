package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}


object BlockPoolNetwork {
  def props(initialBlockPools: Int) = Props(new BlockPoolNetwork(initialBlockPools))
}

class BlockPoolNetwork(initialBlockPools: Int) extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = Actor.emptyBehavior


}
