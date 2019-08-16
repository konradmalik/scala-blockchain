package blockchain.api.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Address, AddressFromURIString, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}
import akka.pattern.ask
import blockchain.Chain
import blockchain.api.actors.BlockchainClusterListener.{GetNodes, RefreshChain}
import blockchain.core.Blockchain

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object BlockchainClusterListener {
  def props(nodeAddress: String) = Props(new BlockchainClusterListener(Some(nodeAddress)))

  def props = Props(new BlockchainClusterListener(None))

  final case object GetNodes

  final case object RefreshChain

}

class BlockchainClusterListener(nodeAddress: Option[String]) extends Actor with ActorLogging {

  import blockchain.api._

  val cluster: Cluster = Cluster(context.system)
  // if existing node provided, then join it, else join itself (important!)
  nodeAddress match {
    case Some(addr) =>
      cluster.join(AddressFromURIString(addr))
    case None => cluster.join(cluster.selfAddress)
  }

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
    case GetNodes => sender ! nodes
    case RefreshChain => sender ! refreshChain
    case _: MemberEvent => log.info("Unknown member event ignored")
  }

  def refreshChain: Int = {
    val setOfFutures: Set[Future[ActorRef]] = nodes.map(a => context.actorSelection(s"${a.toString}/user/$SUPERVISOR_ACTOR_NAME/$BLOCKCHAIN_ACTOR_NAME").resolveOne())
    val futureSetOfBlockchainNodes: Future[Iterable[ActorRef]] = futuresToFuture(setOfFutures)
    // send getChain to each node
    val futureSetOfChainLengths: Future[Iterable[(ActorRef, Int)]] = futureSetOfBlockchainNodes
      .map(_.map(ar => (ar ? BlockchainActor.GetLength).mapTo[Int].map(int => ar -> int)))
      .flatMap(futuresToFuture)
    val futureLongestActor: Future[(ActorRef, Int)] = futureSetOfChainLengths.map(_.head)
    if (nodes.size == 1) {
      Await.result(futureLongestActor, selectionTimeout)._2
    } else {
      // get chain from longest actor
      val futureLongestChain: Future[Chain] = futureLongestActor
        .flatMap(p => (p._1 ? BlockchainActor.GetBlockchain).mapTo[Blockchain].map(_.getBlockchain))
      val futureLongestChainLength: Future[Int] = futureLongestActor.map(_._2)
      // replace chain in shorter actors
      // but we must wait for length and chain here
      val futureActorsToReplace: Future[Iterable[ActorRef]] = for {
        longestActor <- futureLongestActor
        setOfChainLengths <- futureSetOfChainLengths
        longestChainLength <- futureLongestChainLength
      } yield {
        setOfChainLengths.filter(p => p._2 <= longestChainLength && p._1.compareTo(longestActor._1) != 0)
          .map(_._1)
      }
      // await actors to replace and send replace chain
      val futureReplacementResponses: Future[Iterable[(ActorRef, Boolean)]] = (for {
        actors <- futureActorsToReplace
        replacementChain <- futureLongestChain
      } yield {
        Future.sequence(actors.map(actor =>
          (actor ? BlockchainActor.ReplaceChain(replacementChain)).mapTo[Boolean].map(cv => actor -> cv)))
      }).flatMap(identity)
      // based on responses, filter out those that replied false from nodes (can we actually kick them out?)
      val futureFailedUpdates: Future[Iterable[(ActorRef, Boolean)]] = for {
        longestActor <- futureLongestActor
        replacementResponses <- futureReplacementResponses
      } yield {
        replacementResponses.filterNot(p => p._2 || p._1.compareTo(longestActor._1) == 0)
      }
      // now we can kick out nodes which failed
      futureFailedUpdates.map(_.foreach(p => {
        cluster.leave(p._1.path.address)
      }))
      Await.result(futureLongestChainLength, selectionTimeout)
    }
  }

  private def futuresToFuture[T](futures: Iterable[Future[T]]): Future[Iterable[T]] = {
    Future.sequence(futures.map(futureToFutureTry)).map(_.collect { case Success(x) => x })
  }

  private def futureToFutureTry[T](f: Future[T]): Future[Try[T]] = f.map(Success(_)).recover({ case e => Failure(e) })
}
