package io.github.konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Address, Props}
import akka.cluster.{Cluster, MemberStatus}
import akka.cluster.ClusterEvent._
import io.github.konradmalik.blockchain.api.actors.BlockchainClusterListener.GetNodes

object BlockchainClusterListener {
  def props = Props(new BlockchainClusterListener)

  final case class GetNodes(timestamp: Long)
}

class BlockchainClusterListener extends Actor with ActorLogging {

  val cluster: Cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  var nodes = Set.empty[Address]

  def receive: PartialFunction[Any, Unit] = {
    case state: CurrentClusterState =>
      nodes = state.members.collect {
        case m if m.status == MemberStatus.Up => m.address
      }
    case MemberUp(member) =>
      nodes += member.address
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      nodes -= member.address
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case GetNodes(_) => sender ! nodes
    case _: MemberEvent => log.info("Unknown member event ignored")
  }
}
