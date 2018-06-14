package konradmalik.blockchain.crypto

import konradmalik.blockchain.{Bytes, HexString}

trait HashingAlgorithm {
  def hash(input: Bytes): HexString
}

class Hasher(private val hashingAlgorithm: HashingAlgorithm) {

  def hash(input: String): HexString = {
    hashingAlgorithm.hash(input.getBytes("UTF-8"))
  }

  def hash(input: Bytes): HexString = {
    hashingAlgorithm.hash(input)
  }
}
