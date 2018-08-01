package konradmalik.blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import konradmalik.blockchain.api.actors.Supervisor.{InitializedBlockPoolNetwork, InitializedBlockchainNetwork, InitializedPeerNetwork}
import org.scalatest.{FlatSpecLike, Matchers}

class SupervisorTest extends TestKit(ActorSystem("supervisorTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val supervisorActor: ActorRef = system.actorOf(Supervisor.props(), "supervisor")

  "Supervisor" should "be able to start Blockchain Network" in {

    supervisorActor.tell(Supervisor.InitializeBlockchainNetwork(requestId = 0, 0), probe.ref)
    probe.expectMsg(InitializedBlockchainNetwork(0, 0))
  }
  it should "be able to start Peer Network" in {

    supervisorActor.tell(Supervisor.InitializePeerNetwork(requestId = 1, 0), probe.ref)
    probe.expectMsg(InitializedPeerNetwork(1, 0))
  }
  it should "be able to start BlockPool Network" in {

    supervisorActor.tell(Supervisor.InitializeBlockPoolNetwork(requestId = 2, 0), probe.ref)
    probe.expectMsg(InitializedBlockPoolNetwork(2, 0))
  }
  it should "ignore unknown messages" in {

    supervisorActor.tell("random bad message", probe.ref)
    probe.expectNoMessage
  }
}

