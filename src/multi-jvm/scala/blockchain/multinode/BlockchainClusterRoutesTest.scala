package blockchain.multinode

import akka.actor.ActorRef
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import akka.util.Timeout
import blockchain.api.actors.Supervisor.InitializedBlockchain
import blockchain.api.actors.{BlockchainClusterListener, Supervisor}

class BlockChainClusterMultiJvmNode1 extends BlockchainClusterRoutesTest

class BlockChainClusterMultiJvmNode2 extends BlockchainClusterRoutesTest

class BlockchainClusterRoutesTest extends MultiNodeSpec(BlockchainMultiNodeConfig)
  with STMultiNodeSpec with ImplicitSender {

  import BlockchainMultiNodeConfig._

  import scala.concurrent.duration._

  implicit val askTimeout: Timeout = Timeout(10 seconds)

  def initialParticipants: Int = roles.size

  // top level supervisor
  val supervisor: ActorRef = system.actorOf(Supervisor.props(), "supervisor")
  // initialize required children
  supervisor ! Supervisor.InitializeBlockchain
  val blockchain = receiveOne(askTimeout.duration).asInstanceOf[InitializedBlockchain].actor

  "A BlockchainClusterRoutesTest" must {

    "wait for all nodes to enter a barrier" in {
      enterBarrier("startup")
    }

    "join self-single cluster when no nodes provided" in {
      runOn(node1) {
        val blockchainClusterListener: ActorRef = system.actorOf(BlockchainClusterListener.props, name = "blockchainClusterListener")
        enterBarrier("deployed")
        blockchainClusterListener ! BlockchainClusterListener.GetNodes
        expectMsg(askTimeout.duration, Set(node(node1).address))
      }
    }

    "join new cluster when asked" in {
      runOn(node2) {
        val blockchainClusterListener: ActorRef = system.actorOf(BlockchainClusterListener.props(node(node1).address.toString), name = "blockchainClusterListener")
        enterBarrier("deployed")
        Thread.sleep(askTimeout.duration._1 * 1000 / 2)
        blockchainClusterListener ! BlockchainClusterListener.GetNodes
        expectMsg(askTimeout.duration, Set(node(node1).address, node(node2).address))
      }

      runOn(node1) {
        val blockchainClusterListener = system.actorSelection(node(node1) / "user" / "blockchainClusterListener")
        Thread.sleep(askTimeout.duration._1 * 1000 / 2)
        blockchainClusterListener ! BlockchainClusterListener.GetNodes
        expectMsg(askTimeout.duration, Set(node(node1).address, node(node2).address))
      }

    }

    enterBarrier("finished")
  }
}
