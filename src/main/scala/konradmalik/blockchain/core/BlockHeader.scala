package konradmalik.blockchain.core

import java.util.Calendar

import konradmalik.blockchain.HexString
import org.json4s.Extraction

class BlockHeader(val index: Long,
                  val previousHash: HexString,
                  val data: String,
                  val merkleHash: HexString,
                  val timestamp: Long,
                  val nonce: Int) extends Serializable {

  def toJson: String = Extraction.decompose(this).toString

}

object BlockHeader {
  private val now = Calendar.getInstance()

  def apply(index: Long, previousHash: HexString, data: String, merkleHash: HexString, timestamp: Long, nonce: Int): BlockHeader =
    new BlockHeader(index, previousHash, data, merkleHash, timestamp, nonce)

  def apply(index: Long, previousHash: HexString, data: String, merkleHash: HexString, nonce: Int): BlockHeader = {
    val timestamp: Long = now.getTimeInMillis
    BlockHeader(index: Long, previousHash: HexString, data: String, merkleHash: HexString, timestamp: Long, nonce: Int)
  }
}

