package konradmalik.blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{FlatSpecLike, Matchers}

class BlockchainActorTest extends TestKit(ActorSystem("blockchainActorTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val blockchainActor: ActorRef = system.actorOf(BlockchainActor.props("0",2), "blockchainActor")

  "BlockchainActor" should "give length" in {
    blockchainActor.tell(BlockchainActor.GetLength(0), probe.ref)

    probe.expectMsg(BlockchainActor.ChainLength(0, "0", 1))
  }
}

