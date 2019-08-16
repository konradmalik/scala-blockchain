package blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import blockchain.api.actors.Supervisor.{InitializedBlockPool, InitializedBlockchain, InitializedPeer}
import org.scalatest.{FlatSpecLike, Matchers}

class SupervisorTest extends TestKit(ActorSystem("supervisorTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val supervisorActor: ActorRef = system.actorOf(Supervisor.props(), "supervisor")

  "Supervisor" should "be able to start Blockchain" in {

    supervisorActor.tell(Supervisor.InitializeBlockchain, probe.ref)
    probe.expectMsgClass(classOf[InitializedBlockchain])
  }
  it should "be able to start Peer" in {

    supervisorActor.tell(Supervisor.InitializePeer, probe.ref)
    probe.expectMsgClass(classOf[InitializedPeer])
  }
  it should "be able to start BlockPool" in {

    supervisorActor.tell(Supervisor.InitializeBlockPool, probe.ref)
    probe.expectMsgClass(classOf[InitializedBlockPool])
  }
  it should "ignore unknown messages" in {

    supervisorActor.tell("random bad message", probe.ref)
    probe.expectNoMessage
  }
}

