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

  val blockchainClusterListener: ActorRef = system.actorOf(BlockchainClusterListener.props, name = "blockchainClusterListener")
  // top level supervisor
  val supervisor: ActorRef = system.actorOf(Supervisor.props(), "supervisor")
  // initialize required children
  supervisor ! Supervisor.InitializeBlockchain
  val blockchain = receiveOne(askTimeout.duration).asInstanceOf[InitializedBlockchain].actor

  "A BlockchainClusterRoutesTest" must {

    "wait for all nodes to enter a barrier" in {
      enterBarrier("startup")
    }

    "send to and receive from a remote node" in {
      runOn(node1) {
        println(node(node1).address)
        enterBarrier("deployed")

        val otherBCL = system.actorSelection(node(node2) / "user" / "blockchainClusterListener")
        println(otherBCL.pathString)
        blockchainClusterListener ! BlockchainClusterListener.GetNodes
        expectMsg(askTimeout.duration, 2)

      }

      runOn(node2) {
        println(node(node2).address)
        enterBarrier("deployed")
      }

      enterBarrier("finished")
    }
  }
}
