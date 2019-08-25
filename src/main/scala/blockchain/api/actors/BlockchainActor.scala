package blockchain.api.actors

import java.time.Instant

import akka.actor.{Actor, ActorLogging, Props}
import blockchain.Chain
import blockchain.api.ErrorMsg
import blockchain.api.actors.BlockchainActor._
import blockchain.core.Blockchain
import blockchain.protocols.{ProofOfWork, ProofProtocol}

object BlockchainActor {
  def props(difficulty: Int) = Props(new BlockchainActor(new ProofOfWork(difficulty)))

  final case object GetLength

  final case object GetChain

  final case object IsChainValid

  final case object GetLastBlock

  final case class ChainValidity(timestamp: Instant, isChainValid: Boolean)

  final case class MakeNewBlock(data: String)

  final case class ReplaceChain(newChain: Chain)

}

class BlockchainActor(proof: ProofProtocol) extends Blockchain(proof) with Actor with ActorLogging {
  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = {
    case GetLength => sender() ! length
    case GetChain => sender() ! this.getBlockchain
    case GetLastBlock => sender() ! getLastBlock
    case IsChainValid => sender() ! ChainValidity(Instant.now(), isChainValid)
    case ReplaceChain(newChain) =>
      log.info(s"Replacing chain; old: ${this.length}, new: ${newChain.length}")
      val ifReplaced: Boolean = this.replaceBlockchain(newChain)
      sender() ! ifReplaced

    case MakeNewBlock(data) =>
      val block = createNextBlock(data)
      val validBlock = validateBlock(block)
      val isOk = addBlock(validBlock)
      if (isOk) {
        log.info("Added block at: " + block.timestamp + " with data: " + data)
        sender ! validBlock
      }
      else {
        val message = "Could not add block at: " + block.timestamp + " with data: " + data
        log.error(message)
        sender ! ErrorMsg(message)
      }
  }
}
