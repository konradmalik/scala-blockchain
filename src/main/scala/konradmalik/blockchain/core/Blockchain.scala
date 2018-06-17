package konradmalik.blockchain.core

import konradmalik.blockchain.Chain
import konradmalik.blockchain.protocols.ProofProtocol

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class Blockchain(proof: ProofProtocol) {

  // add genesis
  private val genesis = validateBlock(Block(0L, "0" * 64, "Genesis", 0))

  private val chain: Chain = ListBuffer[Block](genesis)

  private def addBlock(block: Block): Unit = {
    chain.append(block)
  }

  def createTransaction() = ???

  def getLastBlock: Block = chain.last

  def isChainValid: Boolean = {
    if (chain.isEmpty) return false
    if (chain.length == 1) return true

    val shiftedPairs: mutable.Seq[(Block, Block)] = chain.dropRight(1) zip chain.tail
    shiftedPairs.forall {
      case (prevB: Block, nextB: Block) =>
        proof.isBlockProven(prevB) &&
          proof.isBlockProven(nextB) &&
          prevB.hashBlock.equals(nextB.previousHash) &&
          prevB.index + 1 == nextB.index
    }

  }

  def validateBlock(block: Block): Block = {
    proof.proveBlock(block)
  }


}

object Blockchain {

}