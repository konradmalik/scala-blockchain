package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}
import konradmalik.blockchain.api.actors.BlockchainActor._
import konradmalik.blockchain.core.{Block, Blockchain}
import konradmalik.blockchain.protocols.{ProofOfWork, ProofProtocol}

object BlockchainActor {
  def props(id: String, difficulty: Int) = Props(new BlockchainActor(id, new ProofOfWork(difficulty)))

  final case class GetLength(requestId: Long)
  final case class ChainLength(requestId: Long, chainId: String, chainLength: Int)
  final case class MakeNewBlock(requestId: Long, data: String)
  final case class BlockAdded(requestId: Long, block: Block)
  final case class ErrorAddingBlock(requestId: Long)
}

class BlockchainActor(id: String, proof: ProofProtocol) extends Blockchain(proof) with Actor with ActorLogging {
  override def preStart(): Unit = log.info("{}-{} started!", this.getClass.getSimpleName, id)

  override def postStop(): Unit = log.info("{}-{} stopped!", this.getClass.getSimpleName, id)

  override def receive: Receive = {
    case GetLength(rId) => sender() ! ChainLength(rId, id, length)

    case MakeNewBlock(rId, data) =>
      val block = createNextBlock(data)
      val validBlock = validateBlock(block)
      val isOk = addBlock(validBlock)
      if(isOk) sender ! BlockAdded(rId, validBlock)
      else sender ! ErrorAddingBlock(rId)
  }
}
