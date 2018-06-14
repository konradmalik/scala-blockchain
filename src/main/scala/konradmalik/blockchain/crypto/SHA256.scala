package konradmalik.blockchain.crypto

import java.security.MessageDigest

import konradmalik.blockchain.{Bytes, HexString}

object SHA256 extends HashingAlgorithm {
  override def hash(input: Bytes): HexString = {
    MessageDigest.getInstance("SHA-256")
      .digest(input)
      .map("%02x".format(_)).mkString
  }
}
