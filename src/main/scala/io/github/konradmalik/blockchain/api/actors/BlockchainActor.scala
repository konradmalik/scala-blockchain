package io.github.konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}
import io.github.konradmalik.blockchain.api.actors.BlockchainActor._
import io.github.konradmalik.blockchain.core.{Block, Blockchain}
import io.github.konradmalik.blockchain.protocols.{ProofOfWork, ProofProtocol}

object BlockchainActor {
  def props(difficulty: Int) = Props(new BlockchainActor(new ProofOfWork(difficulty)))

  final case class GetLength(timestamp: Long)
  final case class GetChain(timestamp: Long)
  final case class IsChainValid(timestamp: Long)
  final case class ChainLength(timestamp: Long, chainLength: Int)
  final case class MakeNewBlock(timestamp: Long, data: String)
  final case class BlockAdded(timestamp: Long, block: Block)
  final case class ErrorAddingBlock(timestamp: Long)
  final case class Chain(timestamp: Long, chain: Blockchain)
  final case class ChainValidity(timestamp: Long, valid: Boolean)
}

class BlockchainActor(proof: ProofProtocol) extends Blockchain(proof) with Actor with ActorLogging {
  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = {
    case GetLength(rId) => sender() ! ChainLength(rId, length)
    case GetChain(rId) => sender() ! Chain(rId, this)
    case IsChainValid(rId) => sender() ! ChainValidity(rId, isChainValid)

    case MakeNewBlock(rId, data) =>
      val block = createNextBlock(data)
      val validBlock = validateBlock(block)
      val isOk = addBlock(validBlock)
      if(isOk) {
        log.info("Added block at: " + rId + " with data: " + data)
        sender ! BlockAdded(rId, validBlock)
      }
      else {
        log.error("Could not add block at: " + rId + " with data: " + data)
        sender ! ErrorAddingBlock(rId)
      }
  }
}
