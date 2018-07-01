package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}

object Blockchain {
  def props(id: String) = Props(new Blockchain(id))
}

class Blockchain(id: String) extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("{}-{} started!", this.getClass.getSimpleName, id)

  override def postStop(): Unit = log.info("{}-{} stopped!", this.getClass.getSimpleName, id)

  override def receive: Receive = Actor.emptyBehavior
}
