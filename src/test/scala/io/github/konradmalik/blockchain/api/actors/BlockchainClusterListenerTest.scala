//package io.github.konradmalik.blockchain.api.actors
//
//import java.util.Properties
//
//import akka.actor.{ActorRef, ActorSystem, Address}
//import akka.remote.testconductor.RoleName
//import akka.remote.testkit.{MultiNodeConfig, MultiNodeSpec}
//import akka.testkit.{TestKit, TestProbe}
//
//object MultiNodeSampleConfig extends MultiNodeConfig {
//  val props: Properties = System.getProperties
//  props.setProperty("multinode.max-nodes", "1")
//  props.setProperty("multinode.host", "127.0.0.1")
//  props.setProperty("multinode.server-host", "127.0.0.1")
//  props.setProperty("multinode.index", "0")
//
//  val node1: RoleName = role("node1")
//}
//
//class BlockchainClusterListenerTest extends MultiNodeSpec(MultiNodeSampleConfig) with STMultiNodeSpec {
//
//  val probe = TestProbe()
//  val blockchainClusterListener: ActorRef = system.actorOf(BlockchainClusterListener.props(""), "blockchainClusterListener")
//
//  import MultiNodeSampleConfig._
//
//  def initialParticipants: Int = roles.size
//
//  "A MultiNodeSample" should "wait for all nodes to enter a barrier" in {
//      enterBarrier("startup")
//  }
//
//
//  "BlockchainClusterListener" should "give length" in {
//    blockchainClusterListener.tell(BlockchainClusterListener.GetNodes(0), probe.ref)
//
//    probe.expectMsgClass(classOf[Set[Address]])
//  }
//}
//