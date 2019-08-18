package blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import blockchain.Chain
import blockchain.api._
import blockchain.core.{Block, Blockchain}
import blockchain.protocols.ProofOfWork
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class BlockchainActorTest extends TestKit(ActorSystem("test")) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val blockchainActor: ActorRef = system.actorOf(BlockchainActor.props(2), "blockchainActor")

  "BlockchainActor" should "give length" in {
    blockchainActor ! BlockchainActor.GetLength

    expectMsg(1)
  }
  it should "mine new block with no problems" in {
    blockchainActor ! BlockchainActor.MakeNewBlock("data")
    val addedBlock = receiveOne(askTimeout.duration).asInstanceOf[Block]
    assert(addedBlock.data == "data")

    blockchainActor ! BlockchainActor.GetLength
    expectMsg(2)
  }
  it should "return list of blocks" in {
    blockchainActor ! BlockchainActor.GetChain
    val chain = receiveOne(askTimeout.duration).asInstanceOf[Chain]
    assert(chain.size == 2)
  }
  it should "return if its valid" in {
    blockchainActor ! BlockchainActor.IsChainValid
    val valid = receiveOne(askTimeout.duration).asInstanceOf[Boolean]
    assert(valid)
  }
  it should "replace its out chain" in {
    val newChain = new Blockchain(new ProofOfWork(3)).getBlockchain

    blockchainActor ! BlockchainActor.ReplaceChain(newChain)
    val valid = receiveOne(askTimeout.duration).asInstanceOf[Boolean]
    assert(valid)
    blockchainActor ! BlockchainActor.GetChain
    val chain = receiveOne(askTimeout.duration).asInstanceOf[Chain]
    assert(chain.size == 1)
  }
}

