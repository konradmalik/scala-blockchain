package io.github.konradmalik.blockchain.crypto

import java.security.MessageDigest

import io.github.konradmalik.blockchain.util.Serialization
import io.github.konradmalik.blockchain.{Bytes, HexString}

object SHA256 extends HashingAlgorithm {
  override def hash(input: Any): HexString = {
    val b: Array[Byte] = input match {
      case x: Byte => Array(x)
      case x: Bytes => x
      case x: String => x.getBytes
      case x: Any => Serialization.serialize(x)
    }

    MessageDigest.getInstance("SHA-256")
      .digest(b)
      .map("%02x".format(_)).mkString
  }
}
