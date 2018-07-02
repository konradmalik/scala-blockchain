package konradmalik.blockchain.api.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{FlatSpecLike, Matchers}

class BlockchainActorTest extends TestKit(ActorSystem("blockchainActorTest")) with FlatSpecLike with Matchers {

  val probe = TestProbe()
  val supervisorActor: ActorRef = system.actorOf(Supervisor.props(), "supervisor")

  "BlockchainActor" should "" in {
  }
}

