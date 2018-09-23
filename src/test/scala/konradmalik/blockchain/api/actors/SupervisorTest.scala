package konradmalik.blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import konradmalik.blockchain.api.actors.Supervisor.{InitializedBlockPool, InitializedBlockchain, InitializedPeer}
import org.scalatest.{FlatSpecLike, Matchers}

class SupervisorTest extends TestKit(ActorSystem("supervisorTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val supervisorActor: ActorRef = system.actorOf(Supervisor.props(), "supervisor")

  "Supervisor" should "be able to start Blockchain" in {

    supervisorActor.tell(Supervisor.InitializeBlockchain(requestId = 0), probe.ref)
    probe.expectMsg(InitializedBlockchain(0))
  }
  it should "be able to start Peer" in {

    supervisorActor.tell(Supervisor.InitializePeer(requestId = 1), probe.ref)
    probe.expectMsg(InitializedPeer(1))
  }
  it should "be able to start BlockPool" in {

    supervisorActor.tell(Supervisor.InitializeBlockPool(requestId = 2), probe.ref)
    probe.expectMsg(InitializedBlockPool(2))
  }
  it should "ignore unknown messages" in {

    supervisorActor.tell("random bad message", probe.ref)
    probe.expectNoMessage
  }
}

