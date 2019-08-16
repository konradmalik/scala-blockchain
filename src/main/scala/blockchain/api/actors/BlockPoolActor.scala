package blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}

object BlockPoolActor {
  def props = Props(new BlockPoolActor)
}

class BlockPoolActor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = Actor.emptyBehavior
}
