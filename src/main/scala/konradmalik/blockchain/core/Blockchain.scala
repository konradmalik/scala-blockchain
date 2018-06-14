package konradmalik.blockchain.core

import konradmalik.blockchain.{Chain, Transactions}
import konradmalik.blockchain.crypto.Hasher

import scala.collection.mutable


class Blockchain(difficulty: Int, proof: ProofProtocol, hasher: Hasher) {

  private val chain: Chain = new Chain

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
        proof.isBlockValid(prevB) &&
          proof.isBlockValid(nextB) &&
          hashBlock(prevB).equals(nextB.previousHash) &&
          prevB.index + 1 == nextB.index
    }

  }

  def hashBlock(block: Block): String = hasher.hash(block.toJson)

  def mineBlock(newData: String, transactions: Transactions) = {
    // use arguments to call Block.mine, get pow from the blockchain
    val lastBlock = getLastBlock

  }


}

object Blockchain {

}