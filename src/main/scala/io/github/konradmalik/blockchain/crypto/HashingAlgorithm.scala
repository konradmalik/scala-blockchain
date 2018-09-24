package io.github.konradmalik.blockchain.crypto

import io.github.konradmalik.blockchain.HexString

trait HashingAlgorithm {
  def hash(input: Any): HexString

  def hashMany(input: Any*): HexString = {
    hash(input.map(hash).reduce(_ + _))
  }
}
