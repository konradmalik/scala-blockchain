package io.github.konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, Props}
import io.github.konradmalik.blockchain.api.ErrorMsg
import io.github.konradmalik.blockchain.api.actors.BlockchainActor._
import io.github.konradmalik.blockchain.core.{Block, Blockchain}
import io.github.konradmalik.blockchain.protocols.{ProofOfWork, ProofProtocol}

object BlockchainActor {
  def props(difficulty: Int) = Props(new BlockchainActor(new ProofOfWork(difficulty)))

  final case class GetLength(timestamp: Long)
  final case class GetChain(timestamp: Long)
  final case class IsChainValid(timestamp: Long)
  final case class GetLastBlock(timestamp: Long)
  final case class ChainLength(timestamp: Long, chainLength: Int)
  final case class MakeNewBlock(timestamp: Long, data: String)
  final case class BlockMsg(timestamp: Long, block: Block)
  final case class Chain(timestamp: Long, chain: Blockchain)
  final case class ChainValidity(timestamp: Long, valid: Boolean)
  final case class ReplaceChain(timestamp: Long, newChain: List[Block])
}

class BlockchainActor(proof: ProofProtocol) extends Blockchain(proof) with Actor with ActorLogging {
  override def preStart(): Unit = log.info("{} started!", this.getClass.getSimpleName)

  override def postStop(): Unit = log.info("{} stopped!", this.getClass.getSimpleName)

  override def receive: Receive = {
    case GetLength(rId) => sender() ! ChainLength(rId, length)
    case GetChain(rId) => sender() ! Chain(rId, this)
    case GetLastBlock(rId) => sender() ! BlockMsg(rId, getLastBlock)
    case IsChainValid(rId) => sender() ! ChainValidity(rId, isChainValid)
    case ReplaceChain(ts, newChain) =>
      log.info(s"Replacing chain; old: ${this.length}, new: ${newChain.length}")
      val ifReplaced: Boolean = this.replaceBlockchain(newChain)
      sender() ! ChainValidity(ts, ifReplaced)

    case MakeNewBlock(rId, data) =>
      val block = createNextBlock(data)
      val validBlock = validateBlock(block)
      val isOk = addBlock(validBlock)
      if(isOk) {
        log.info("Added block at: " + rId + " with data: " + data)
        sender ! BlockMsg(rId, validBlock)
      }
      else {
        log.error("Could not add block at: " + rId + " with data: " + data)
        sender ! ErrorMsg(rId)
      }
  }
}
