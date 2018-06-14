package konradmalik.blockchain.core

import scala.util.control.Breaks._

trait ProofProtocol {

  def generateProof(previousProof: Long)(implicit mineDifficulty: Int, hashingFunction: String => String): Long

  def isBlockValid(block: Block)(implicit mineDifficulty: Int, hashingFunction: String => String): Boolean

}

class ProofOfWork(implicit val mineDifficulty: Int){

}

object ProofOfWork extends ProofProtocol {
  private val mathTask: (Long, Long) => Double = (a, b) => Math.pow(a, 2) - Math.pow(b, 2)
  private implicit val mineDifficulty: Int = 4

  override def generateProof(previousProof: Long)(implicit mineDifficulty: Int, hashingFunction: String => String): Long = {

    var proof = 1L
    val template = "0" * mineDifficulty

    while (true) {
      val hash = hashingFunction(mathTask(previousProof, proof).toString)
      if (hash.substring(0, mineDifficulty).equals(template))
        break
      else
        proof += 1
    }

    proof
  }


  override def isBlockValid(block: Block)(implicit mineDifficulty: Int, hashingFunction: String => String): Boolean = {
    val template = "0" * mineDifficulty
    Blockchain.hashBlock(block).substring(0, mineDifficulty).equals(template)
  }

}
