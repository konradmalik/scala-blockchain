package konradmalik.blockchain.core

import java.beans.Transient
import java.util.Calendar

import konradmalik.blockchain.crypto.Hasher
import konradmalik.blockchain.{HexString, Transactions}
import org.json4s.Extraction

class Block(val hasher: Hasher,
            val index: Long,
            val previousHash: HexString,
            val data: String,
            val merkleHash: HexString,
            val timestamp: Long,
            val nonce: Int,
            @Transient
            val transactions: Transactions) extends Serializable {

  val hash: HexString = hashBlock

  def hashBlock: HexString = {
    hasher.hashMany(index, previousHash, data, merkleHash, timestamp, nonce)
  }

  def hasValidHash: Boolean = {
    hashBlock.equals(hash)
  }

  def hasValidMerkleHash: Boolean = {
    Merkle.computeRoot(transactions).equals(merkleHash)
  }

  /** String representation of the block */
  def toJson: String = Extraction.decompose(this).toString

}

object Block {
  private val now = Calendar.getInstance()

  def apply(hasher: Hasher, index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int, transactions: Transactions): Block =
    new Block(hasher, index, previousHash, data, Merkle.computeRoot(transactions), timestamp, nonce, transactions)

  def apply(hasher: Hasher, index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int): Block = {
    val txs = new Transactions
    new Block(hasher, index, previousHash, data, Merkle.computeRoot(txs), timestamp, nonce, txs)
  }

  def apply(hasher: Hasher, index: Long, previousHash: HexString, data: String, nonce: Int, transactions: Transactions): Block = {
    val timestamp: Long = now.getTimeInMillis
    new Block(hasher, index, previousHash, data, Merkle.computeRoot(transactions), timestamp, nonce, transactions)
  }

  def apply(hasher: Hasher, index: Long, previousHash: HexString, data: String, nonce: Int): Block = {
    val txs = new Transactions
    val timestamp: Long = now.getTimeInMillis
    new Block(hasher, index, previousHash, data, Merkle.computeRoot(txs), timestamp, nonce, txs)
  }
}