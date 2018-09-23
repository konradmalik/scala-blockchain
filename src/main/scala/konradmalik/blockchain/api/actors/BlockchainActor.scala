package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}
import konradmalik.blockchain.api.actors.BlockchainActor._
import konradmalik.blockchain.core.{Block, Blockchain}
import konradmalik.blockchain.protocols.{ProofOfWork, ProofProtocol}

object BlockchainActor {
  def props(difficulty: Int) = Props(new BlockchainActor(new ProofOfWork(difficulty)))

  final case class GetLength(requestId: Long)
  final case class GetChain(requestId: Long)
  final case class ChainLength(requestId: Long, chainLength: Int)
  final case class MakeNewBlock(requestId: Long, data: String)
  final case class BlockAdded(requestId: Long, block: Block)
  final case class ErrorAddingBlock(requestId: Long)
  final case class Chain(requestId: Long, chain: Blockchain)
}

class BlockchainActor(proof: ProofProtocol) extends Blockchain(proof) with Actor with ActorLogging {
  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = {
    case GetLength(rId) => sender() ! ChainLength(rId, length)
    case GetChain(rId) => sender() ! Chain(rId, this)

    case MakeNewBlock(rId, data) =>
      val block = createNextBlock(data)
      val validBlock = validateBlock(block)
      val isOk = addBlock(validBlock)
      if(isOk) sender ! BlockAdded(rId, validBlock)
      else sender ! ErrorAddingBlock(rId)
  }
}
