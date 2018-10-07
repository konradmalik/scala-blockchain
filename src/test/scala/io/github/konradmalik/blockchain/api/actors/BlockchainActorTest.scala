package io.github.konradmalik.blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{FlatSpecLike, Matchers}
import io.github.konradmalik.blockchain.api._
import io.github.konradmalik.blockchain.core.Blockchain
import io.github.konradmalik.blockchain.protocols.ProofOfWork

class BlockchainActorTest extends TestKit(ActorSystem("blockchainActorTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val blockchainActor: ActorRef = system.actorOf(BlockchainActor.props(2), "blockchainActor")

  "BlockchainActor" should "give length" in {
    blockchainActor.tell(BlockchainActor.GetLength(0), probe.ref)

    probe.expectMsg(BlockchainActor.ChainLength(0, 1))
  }
  it should "mine new block with no problems" in {
    blockchainActor.tell(BlockchainActor.MakeNewBlock(1, "data"), probe.ref)
    val addedBlock = probe.receiveOne(askTimeout.duration).asInstanceOf[BlockchainActor.BlockMsg]
    assert(addedBlock.timestamp == 1)

    blockchainActor.tell(BlockchainActor.GetLength(2), probe.ref)
    probe.expectMsg(BlockchainActor.ChainLength(2, 2))
  }
  it should "return list of blocks" in {
    blockchainActor.tell(BlockchainActor.GetChain(3), probe.ref)
    val chain = probe.receiveOne(askTimeout.duration).asInstanceOf[BlockchainActor.Chain]
    assert(chain.chain.getBlockchain.size == 2)
  }
  it should "return if its valid" in {
    blockchainActor.tell(BlockchainActor.IsChainValid(4), probe.ref)
    val valid = probe.receiveOne(askTimeout.duration).asInstanceOf[BlockchainActor.ChainValidity]
    assert(valid.valid)
  }
  it should "replace its out chain" in {
    val newChain = new Blockchain(new ProofOfWork(3)).getBlockchain

    blockchainActor.tell(BlockchainActor.ReplaceChain(5,newChain), probe.ref)
    val valid = probe.receiveOne(askTimeout.duration).asInstanceOf[BlockchainActor.ChainValidity]
    assert(valid.valid)
    blockchainActor.tell(BlockchainActor.GetChain(6), probe.ref)
    val chain = probe.receiveOne(askTimeout.duration).asInstanceOf[BlockchainActor.Chain]
    assert(chain.chain.getBlockchain.size == 1)  }
}
