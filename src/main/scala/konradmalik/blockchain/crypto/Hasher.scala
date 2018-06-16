package konradmalik.blockchain.crypto

import konradmalik.blockchain.util.Serialization
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

  def hash(input: Any): HexString = {
    hashingAlgorithm.hash(Serialization.serialize(input))
  }

  def hashMany(inputs: Any*): HexString = {
    hash(inputs.map(hash).reduce(_ + _))
  }
}
