package blockchain.crypto

import blockchain.HexString

trait HashingAlgorithm {
  def hash(input: Any): HexString

  def hashMany(input: Any*): HexString = {
    hash(input.map(hash).reduce(_ + _))
  }
}
