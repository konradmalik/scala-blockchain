package konradmalik.blockchain.protocols

import konradmalik.blockchain.core.Block
import org.scalatest.{FlatSpec, Matchers}

class ProofOfWorkTest extends FlatSpec with Matchers {

  val pow = ProofOfWork(3)
  val block = Block(0, "0" * 64, "Test", 0)

  "Proof of work" should "be able to prove blocks" in {
    assert(!pow.isBlockProven(block))
    val notProvenHash = block.hash
    notProvenHash should not startWith "000"

    val proven = pow.proveBlock(block)
    assert(pow.isBlockProven(proven))
    val provenHash = proven.hash
    provenHash should startWith("000")
  }

}