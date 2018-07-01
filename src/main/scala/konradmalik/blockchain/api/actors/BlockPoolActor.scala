package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}

object BlockPoolActor {
  def props(id: String) = Props(new BlockPoolActor(id))
}

class BlockPoolActor(id: String) extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("{}-{} started!", this.getClass.getSimpleName, id)

  override def postStop(): Unit = log.info("{}-{} stopped!", this.getClass.getSimpleName, id)

  override def receive: Receive = Actor.emptyBehavior
}
