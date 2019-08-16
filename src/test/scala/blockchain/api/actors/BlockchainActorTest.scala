package blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import blockchain.api._
import blockchain.core.{Block, Blockchain}
import blockchain.protocols.ProofOfWork
import org.scalatest.{FlatSpecLike, Matchers}

class BlockchainActorTest extends TestKit(ActorSystem("blockchainActorTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val blockchainActor: ActorRef = system.actorOf(BlockchainActor.props(2), "blockchainActor")

  "BlockchainActor" should "give length" in {
    blockchainActor.tell(BlockchainActor.GetLength, probe.ref)

    probe.expectMsg(1)
  }
  it should "mine new block with no problems" in {
    blockchainActor.tell(BlockchainActor.MakeNewBlock("data"), probe.ref)
    val addedBlock = probe.receiveOne(askTimeout.duration).asInstanceOf[Block]
    assert(addedBlock.data == "data")

    blockchainActor.tell(BlockchainActor.GetLength, probe.ref)
    probe.expectMsg(2)
  }
  it should "return list of blocks" in {
    blockchainActor.tell(BlockchainActor.GetBlockchain, probe.ref)
    val chain = probe.receiveOne(askTimeout.duration).asInstanceOf[Blockchain]
    assert(chain.getBlockchain.size == 2)
  }
  it should "return if its valid" in {
    blockchainActor.tell(BlockchainActor.IsChainValid, probe.ref)
    val valid = probe.receiveOne(askTimeout.duration).asInstanceOf[Boolean]
    assert(valid)
  }
  it should "replace its out chain" in {
    val newChain = new Blockchain(new ProofOfWork(3)).getBlockchain

    blockchainActor.tell(BlockchainActor.ReplaceChain(newChain), probe.ref)
    val valid = probe.receiveOne(askTimeout.duration).asInstanceOf[Boolean]
    assert(valid)
    blockchainActor.tell(BlockchainActor.GetBlockchain, probe.ref)
    val chain = probe.receiveOne(askTimeout.duration).asInstanceOf[Blockchain]
    assert(chain.getBlockchain.size == 1)
  }
}

