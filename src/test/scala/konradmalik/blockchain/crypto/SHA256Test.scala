package konradmalik.blockchain.crypto

import org.scalatest.{FlatSpec, Matchers}

class SHA256Test extends FlatSpec with Matchers {

  "SHA256" should "properly generate HexStrings" in {
    SHA256.hash("Test") shouldBe SHA256.hash("Test".getBytes)
    SHA256.hash("Test") shouldBe "532EAABD9574880DBF76B9B8CC00832C20A6EC113D682299550D7A6E0F345E25".toLowerCase
  }
}