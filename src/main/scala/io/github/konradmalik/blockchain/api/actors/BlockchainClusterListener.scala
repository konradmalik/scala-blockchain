package io.github.konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Address, AddressFromURIString, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}
import io.github.konradmalik.blockchain.api.actors.BlockchainClusterListener.GetNodes

object BlockchainClusterListener {
  def props(nodeAddress: String) = Props(new BlockchainClusterListener(nodeAddress))

  final case class GetNodes(timestamp: Long)

}

class BlockchainClusterListener(nodeAddress: String) extends Actor with ActorLogging {

  val cluster: Cluster = Cluster(context.system)
  // if existing node provided, then join it, else join itself (important!)
  if (nodeAddress.nonEmpty)
    cluster.join(AddressFromURIString(nodeAddress))
  else
    cluster.join(cluster.selfAddress)

  var nodes = Set.empty[Address]

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

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
