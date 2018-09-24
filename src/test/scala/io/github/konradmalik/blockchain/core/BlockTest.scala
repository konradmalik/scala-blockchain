package io.github.konradmalik.blockchain.core

import org.scalatest.{FlatSpec, Matchers}

class BlockTest extends FlatSpec with Matchers {

  val block = Block(0, "0" * 64, "Test", 0)

  "Block" should "create valid blocks." in {
    assert(block.hasValidHash)
  }

}