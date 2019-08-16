package blockchain.core

import blockchain.protocols.ProofOfWork
import org.scalatest.{FlatSpec, Matchers}

class BlockchainTest extends FlatSpec with Matchers {

  val pow = ProofOfWork(3)
  val bc = new Blockchain(pow)
  val gBlock: Block = bc.getLastBlock


  "A new Blockchain" should "contain only the genesis block" in {
    bc.getLastBlock.index shouldBe 0
    bc.getLastBlock.previousHash shouldBe "0" * 64
    bc.getBlockchain.length == 1
    bc.getLastBlock shouldBe bc.getBlockchain.last
  }

  "A new Blockchain" should "be valid" in {
    assert(bc.isChainValid)
  }

  "Users" should "not be able to remove blocks" in {
    bc.getBlockchain.drop(1)
    bc.getBlockchain.length == 1
  }

  "Valid link between" should "be properly checked" in {
    val block2 = Block(2, gBlock.hash, "data", 0)
    assert(!Blockchain.isValidLinkBetween(gBlock, block2))

    val block3 = Block(1, "1" * 64, "data", 0)
    assert(!Blockchain.isValidLinkBetween(gBlock, block3))

    val block4 = Block(1, gBlock.hash, "data", 0)
    assert(Blockchain.isValidLinkBetween(gBlock, block4))
  }

  "Blockchain" should "create proper new blocks" in {
    val newBlock = bc.createNextBlock("data1")
    val lastBlock = bc.getLastBlock
    assert(Blockchain.isValidLinkBetween(lastBlock, newBlock))
    assert(newBlock.hasValidHash)
  }

  "Only valid blocks" should "be added" in {
    val newBlock: Block = bc.createNextBlock("Some data 1")
    assert(!bc.addBlock(newBlock))

    val newValidBlock = bc.validateBlock(newBlock)
    assert(bc.addBlock(newValidBlock))
    bc.getLastBlock shouldBe newValidBlock
    bc.getBlockchain.length shouldBe 2

    val newBlock2 = bc.createNextBlock("Some data 2")
    assert(bc.validateAndAddBlock(newBlock2))
    bc.getLastBlock should not be newBlock2
    bc.getLastBlock.previousHash shouldBe newBlock2.previousHash
    bc.getLastBlock.data shouldBe newBlock2.data
    bc.getLastBlock.index shouldBe newBlock2.index
    bc.getBlockchain.length shouldBe 3
  }

  "Blockchain's list of blocks" should "be replaceable" in {
    assert(bc.replaceBlockchain(List.empty[Block]))
    bc.length shouldBe 0

    assert(!bc.replaceBlockchain(List(gBlock, gBlock)))
    bc.length shouldBe 0

    assert(bc.replaceBlockchain(List(gBlock, bc.validateBlock(Block(1, gBlock.hash, "data", 0)))))
    bc.length shouldBe 2

  }

}