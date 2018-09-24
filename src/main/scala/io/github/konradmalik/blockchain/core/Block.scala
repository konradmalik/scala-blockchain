package io.github.konradmalik.blockchain.core

import java.util.Calendar

import io.github.konradmalik.blockchain._
import io.github.konradmalik.blockchain.api.routes.JsonSupport
import io.github.konradmalik.blockchain.crypto.SHA256
import spray.json._

class Block(val index: Long,
            val previousHash: HexString,
            val data: String,
            val timestamp: Long,
            val nonce: Int) extends Serializable {

  val hash: HexString = hashBlock

  def hasValidHash: Boolean = {
    hashBlock.equals(hash)
  }

  def hashBlock: HexString = {
    SHA256.hash(index, previousHash, data, timestamp, nonce)
  }

  /** String representation of the block */
  override def toString: String = this.toJson.compactPrint
}

object Block extends JsonSupport {
  def apply(index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int): Block =
    new Block(index, previousHash, data, timestamp, nonce)

  def apply(index: Long, previousHash: HexString, data: String, nonce: Int): Block = {
    val timestamp: Long = Calendar.getInstance().getTimeInMillis
    new Block(index, previousHash, data, timestamp, nonce)
  }

}




