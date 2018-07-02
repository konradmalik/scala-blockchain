package konradmalik.blockchain.core

import konradmalik.blockchain.protocols.ProofProtocol
import konradmalik.blockchain.{Chain, Transactions}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer


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

  def length: Int = chain.length

  def isChainValid: Boolean = {
    val chain = getBlockchain

    @tailrec
    def checkChain(chain: List[Block]): Boolean = {
      chain match {
        case Nil => false
        case g +: Nil => g.previousHash.equals("0" * 64) && g.index == 0 && isBlockValid(g)
        case a +: b +: tail => isBlockValid(a) && isBlockValid(b) && Blockchain.isValidLinkBetween(a, b) && checkChain(tail)
      }
    }
    // start
    checkChain(chain)
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

  def createNextBlock(data: String, transactions: Transactions): Block = {
    val lb = getLastBlock
    Block(lb.index + 1, lb.hash, data, 0, transactions)
  }

}

object Blockchain {
  def isValidLinkBetween(earlier: Block, next: Block): Boolean = {
    earlier.hash.equals(next.previousHash) && earlier.index + 1 == next.index
  }

}

