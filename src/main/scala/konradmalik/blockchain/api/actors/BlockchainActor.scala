package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}
import konradmalik.blockchain.api.actors.BlockchainActor.{ChainLength, GetLength}
import konradmalik.blockchain.core.Blockchain
import konradmalik.blockchain.protocols.{ProofOfWork, ProofProtocol}

object BlockchainActor {
  def props(id: String, difficulty: Int) = Props(new BlockchainActor(id, new ProofOfWork(difficulty)))

  final case class GetLength(requestId: Long)
  final case class ChainLength(requestId: Long, chainId: String, chainLength: Int)
}

class BlockchainActor(id: String, proof: ProofProtocol) extends Blockchain(proof) with Actor with ActorLogging {
  override def preStart(): Unit = log.info("{}-{} started!", this.getClass.getSimpleName, id)

  override def postStop(): Unit = log.info("{}-{} stopped!", this.getClass.getSimpleName, id)

  override def receive: Receive = {
    case GetLength(rId) => sender() ! ChainLength(rId, id, length)
  }
}
