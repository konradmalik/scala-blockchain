package konradmalik.blockchain.protocols

import konradmalik.blockchain.core.Block

class ProofOfWork(difficulty: Int) extends ProofProtocol {

  // mining
  override def proveBlock(block: Block): Block = {

    var provenBlock = block
    while (!isBlockValid(provenBlock)) {
      provenBlock =
        Block(provenBlock.index, provenBlock.previousHash,
          provenBlock.data, provenBlock.timestamp, provenBlock.nonce + 1, provenBlock.transactions)
    }

    provenBlock
  }


  private def isHashProved(block: Block): Boolean = {
    val blockHash = block.hash
    blockHash.startsWith("0" * difficulty, 0)
  }

  override def isBlockValid(block: Block): Boolean = {
    block.hasValidMerkleHash && block.hasValidHash && isHashProved(block)
  }

}

object ProofOfWork {
  def apply(difficulty: Int) = new ProofOfWork(difficulty)
}
