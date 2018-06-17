package konradmalik.blockchain.core

import java.util.Calendar

import konradmalik.blockchain.crypto.SHA256
import konradmalik.blockchain.{HexString, Transactions, _}
import org.json4s.JsonAST.{JField, JObject, JString}
import org.json4s.native.JsonMethods.{compact, render}
import org.json4s.{CustomSerializer, Extraction, JValue}

class Block(val index: Long,
            val previousHash: HexString,
            val data: String,
            val merkleHash: HexString,
            val timestamp: Long,
            val nonce: Int,
            val transactions: Transactions) extends Serializable {

  val hash: HexString = hashBlock

  def hashBlock: HexString = {
    SHA256.hash(index, previousHash, data, merkleHash, timestamp, nonce)
  }

  def hasValidHash: Boolean = {
    hashBlock.equals(hash)
  }

  def hasValidMerkleHash: Boolean = {
    Merkle.computeRoot(transactions).equals(merkleHash)
  }

  /** String representation of the block */
  def toJson: JValue = Extraction.decompose(this)// ++ JField("hash", JString(hash))

  override def toString: String = compact(render(toJson))


}

object Block {
  def apply(index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int, transactions: Transactions): Block =
    new Block(index, previousHash, data, Merkle.computeRoot(transactions), timestamp, nonce, transactions)

  def apply(index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int): Block = {
    val txs = new Transactions
    new Block(index, previousHash, data, Merkle.computeRoot(txs), timestamp, nonce, txs)
  }

  def apply(index: Long, previousHash: HexString, data: String, nonce: Int, transactions: Transactions): Block = {
    val timestamp: Long = Calendar.getInstance().getTimeInMillis
    new Block(index, previousHash, data, Merkle.computeRoot(transactions), timestamp, nonce, transactions)
  }

  def apply(index: Long, previousHash: HexString, data: String, nonce: Int): Block = {
    val txs = new Transactions
    val timestamp: Long = Calendar.getInstance().getTimeInMillis
    new Block(index, previousHash, data, Merkle.computeRoot(txs), timestamp, nonce, txs)
  }
}

