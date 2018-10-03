package io.github.konradmalik.blockchain.core

import io.github.konradmalik.blockchain.protocols.ProofProtocol
import io.github.konradmalik.blockchain.Chain
import io.github.konradmalik.blockchain.api.routes.JsonSupport

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import spray.json._


class Blockchain(proof: ProofProtocol) {

  // add genesis
  private val genesis = validateBlock(Block(0L, "0" * 64, "Genesis", 0))

  private val chain: Chain = ListBuffer[Block](genesis)

  def addBlock(block: Block): Boolean = {
    if (!isBlockValid(block) || !Blockchain.isValidLinkBetween(getLastBlock, block))
      false
    else {
      chain.append(block)
      true
    }
  }

  def getLastBlock: Block = getBlockchain.last

  def getBlockchain: List[Block] = chain.result()

  def replaceBlockchain(newChain: List[Block]): Boolean = {
    if(!checkChain(newChain))
      false
    else {
      chain.clear()
      chain.append(newChain: _*)
      true
    }
  }


  def length: Int = chain.length

  def isChainValid: Boolean = {
    val chain = getBlockchain

    // start
    checkChain(chain)
  }

  @tailrec
  private def checkChain(chain: List[Block]): Boolean = {
    chain match {
      case Nil => true
      case g +: Nil => g.previousHash.equals("0" * 64) && g.index == 0 && isBlockValid(g)
      case a +: b +: tail => isBlockValid(a) && isBlockValid(b) && Blockchain.isValidLinkBetween(a, b) && checkChain(tail)
    }
  }

  def validateBlock(block: Block): Block = {
    proof.proveBlock(block)
  }

  def isBlockValid(block: Block): Boolean = {
    proof.isBlockValid(block)
  }

  def validateAndAddBlock(block: Block): Boolean = {
    addBlock(validateBlock(block))
  }

  def createNextBlock(data: String): Block = {
    val lb = getLastBlock
    Block(lb.index + 1, lb.hash, data, 0)
  }

  override def toString: String = this.toJson.compactPrint
}

object Blockchain extends JsonSupport {
  def isValidLinkBetween(earlier: Block, next: Block): Boolean = {
    earlier.hash.equals(next.previousHash) && earlier.index + 1 == next.index
  }

}

