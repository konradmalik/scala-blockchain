package blockchain.core

import java.util.Calendar

import blockchain.HexString
import blockchain.crypto.SHA256
import blockchain.json.JsonSupport

class Block(val index: Long,
            val previousHash: HexString,
            val data: String,
            val timestamp: Long,
            val nonce: Int) extends JsonSupport with Serializable {

  val hash: HexString = hashBlock

  def hasValidHash: Boolean = {
    hashBlock.equals(hash)
  }

  def hashBlock: HexString = {
    SHA256.hash(index, previousHash, data, timestamp, nonce)
  }

  /** String representation of the block */
  override def toString: String = blockJsonWriter.write(this).compactPrint

}

object Block {
  def apply(index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int): Block =
    new Block(index, previousHash, data, timestamp, nonce)

  def apply(index: Long, previousHash: HexString, data: String, nonce: Int): Block = {
    val timestamp: Long = Calendar.getInstance().getTimeInMillis
    new Block(index, previousHash, data, timestamp, nonce)
  }

}




