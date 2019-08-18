package blockchain.core

import java.util.Calendar

import blockchain.HexString
import blockchain.crypto.SHA256

case class Block(index: Long,
                 previousHash: HexString,
                 data: String,
                 timestamp: Long,
                 nonce: Int) {

  val hash: HexString = hashBlock

  def hasValidHash: Boolean = {
    hashBlock.equals(hash)
  }

  def hashBlock: HexString = {
    SHA256.hash(index, previousHash, data, timestamp, nonce)
  }

}

object Block {
  def apply(index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int): Block =
    new Block(index, previousHash, data, timestamp, nonce)

  def apply(index: Long, previousHash: HexString, data: String, nonce: Int): Block = {
    val timestamp: Long = Calendar.getInstance().getTimeInMillis
    new Block(index, previousHash, data, timestamp, nonce)
  }

}




