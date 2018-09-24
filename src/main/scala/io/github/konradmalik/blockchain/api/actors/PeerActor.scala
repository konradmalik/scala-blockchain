package io.github.konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}

object PeerActor {
  def props = Props(new PeerActor)
}

class PeerActor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = Actor.emptyBehavior
}
