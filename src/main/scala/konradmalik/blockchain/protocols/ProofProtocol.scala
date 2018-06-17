package konradmalik.blockchain.protocols

import konradmalik.blockchain.core.Block

trait ProofProtocol {

  def proveBlock(block: Block): Block

  def isBlockValid(block: Block): Boolean

}