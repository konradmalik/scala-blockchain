package konradmalik.blockchain.crypto

import konradmalik.blockchain.HexString

trait HashingAlgorithm {
  def hash(input: Any): HexString

  def hashMany(input: Any*): HexString = {
    hash(input.map(hash).reduce(_ + _))
  }
}
