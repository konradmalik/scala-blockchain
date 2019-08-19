package blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import blockchain.api.actors.Supervisor.{InitializedBlockPool, InitializedBlockchain, InitializedPeer}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class SupervisorTest extends TestKit(ActorSystem("test")) with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val supervisorActor: ActorRef = system.actorOf(Supervisor.props, "supervisor")

  "Supervisor" should "be able to start Blockchain" in {

    supervisorActor ! Supervisor.InitializeBlockchain
    expectMsgClass(classOf[InitializedBlockchain])
  }
  it should "be able to start Peer" in {

    supervisorActor ! Supervisor.InitializePeer
    expectMsgClass(classOf[InitializedPeer])
  }
  it should "be able to start BlockPool" in {

    supervisorActor ! Supervisor.InitializeBlockPool
    expectMsgClass(classOf[InitializedBlockPool])
  }
  it should "ignore unknown messages" in {

    supervisorActor ! "random bad message"
    expectNoMessage
  }
}

