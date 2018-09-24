package io.github.konradmalik.blockchain.protocols

import io.github.konradmalik.blockchain.core.Block

trait ProofProtocol {

  def proveBlock(block: Block): Block

  def isBlockValid(block: Block): Boolean

}