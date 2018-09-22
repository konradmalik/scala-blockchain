package konradmalik.blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{FlatSpecLike, Matchers}
import konradmalik.blockchain.api._

class BlockchainActorTest extends TestKit(ActorSystem("blockchainActorTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val blockchainActor: ActorRef = system.actorOf(BlockchainActor.props("0", 2), "blockchainActor")

  "BlockchainActor" should "give length" in {
    blockchainActor.tell(BlockchainActor.GetLength(0), probe.ref)

    probe.expectMsg(BlockchainActor.ChainLength(0, "0", 1))
  }
  it should "mine new block with no problems" in {
    blockchainActor.tell(BlockchainActor.MakeNewBlock(1, "data"), probe.ref)
    val addedBlock = probe.receiveOne(timeout.duration).asInstanceOf[BlockchainActor.BlockAdded]
    assert(addedBlock.requestId == 1)

    blockchainActor.tell(BlockchainActor.GetLength(2), probe.ref)
    probe.expectMsg(BlockchainActor.ChainLength(2, "0", 2))
  }
}

