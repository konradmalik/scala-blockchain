package blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{FlatSpecLike, Matchers}


class BlockchainClusterListenerTest extends TestKit(ActorSystem("blockchainClusterListenerTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val blockchainClusterListener: ActorRef = system.actorOf(BlockchainClusterListener.props(""), "blockchainClusterListener")

}

