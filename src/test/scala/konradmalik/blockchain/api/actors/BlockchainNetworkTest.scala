package konradmalik.blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.concurrent.duration._

class BlockchainNetworkTest extends TestKit(ActorSystem("blockchainNetworkTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val blockchainNetwork0: ActorRef = system.actorOf(BlockchainNetwork.props(0), "blockchain-network0")
  val blockchainNetwork1: ActorRef = system.actorOf(BlockchainNetwork.props(1), "blockchain-network1")
  val blockchainNetwork2: ActorRef = system.actorOf(BlockchainNetwork.props(2), "blockchain-network2")

  "BlockchainNetwork" should "create specified number of blockchains" in {
    blockchainNetwork0.tell(BlockchainNetwork.GetAllChains(0), probe.ref)
    probe.expectMsg(BlockchainNetwork.AllChains(requestId = 0, Map.empty[String, ActorRef]))

    blockchainNetwork1.tell(BlockchainNetwork.GetAllChains(1), probe.ref)

    val msg1 = probe.receiveOne(1 second).asInstanceOf[BlockchainNetwork.AllChains]
    msg1.requestId shouldBe 1
    msg1.blockchainIdToActor.size shouldBe 1

    blockchainNetwork2.tell(BlockchainNetwork.GetAllChains(2), probe.ref)

    val msg2 = probe.receiveOne(1 second).asInstanceOf[BlockchainNetwork.AllChains]
    msg2.requestId shouldBe 2
    msg2.blockchainIdToActor.size shouldBe 2
  }
  it should "be able to get blockchain by id" in {
    blockchainNetwork2.tell(BlockchainNetwork.GetChainById(3, "1"), probe.ref)

    val msg = probe.receiveOne(1 second).asInstanceOf[BlockchainNetwork.Chain]
    msg.requestId shouldBe 3
  }
  it should "return proper message when no blockchains are available" in {
    blockchainNetwork2.tell(BlockchainNetwork.GetChainById(4, "111"), probe.ref)

    val msg = probe.receiveOne(1 second).asInstanceOf[BlockchainNetwork.ChainNotFound]
    msg.requestId shouldBe 4
  }
  it should "return not found when getting longest chain without chains" in {
    blockchainNetwork0.tell(BlockchainNetwork.GetLongestChain(5), probe.ref)

    // long wait due to chain difficulty
    val msg = probe.receiveOne(10 second).asInstanceOf[BlockchainNetwork.ChainNotFound]
    msg.requestId shouldBe 5
  }
  it should "be able to get longest blockchain when there is one" in {
    blockchainNetwork1.tell(BlockchainNetwork.GetLongestChain(6), probe.ref)

    // long wait due to chain difficulty
    val msg = probe.receiveOne(10 second).asInstanceOf[BlockchainNetwork.Chain]
    msg.requestId shouldBe 6
  }
}
