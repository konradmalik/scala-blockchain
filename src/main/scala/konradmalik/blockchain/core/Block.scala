package konradmalik.blockchain.core

import java.util.Calendar

import konradmalik.blockchain.crypto.SHA256
import konradmalik.blockchain._
import org.json4s.native.JsonMethods.{compact, render}
import org.json4s.{Extraction, JValue}

class Block(val index: Long,
            val previousHash: HexString,
            val data: String,
            val timestamp: Long,
            val nonce: Int) extends Serializable {

  val hash: HexString = hashBlock

  def hashBlock: HexString = {
    SHA256.hash(index, previousHash, data, timestamp, nonce)
  }

  def hasValidHash: Boolean = {
    hashBlock.equals(hash)
  }

  /** String representation of the block */
  def toJson: JValue = Extraction.decompose(this) // ++ JField("hash", JString(hash))

  override def toString: String = compact(render(toJson))


}

object Block {
  def apply(index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int): Block =
    new Block(index, previousHash, data, timestamp, nonce)

  def apply(index: Long, previousHash: HexString, data: String, nonce: Int): Block = {
    val timestamp: Long = Calendar.getInstance().getTimeInMillis
    new Block(index, previousHash, data, timestamp, nonce)
  }
}

