package konradmalik.blockchain.core

import konradmalik.blockchain.crypto.SHA256
import konradmalik.blockchain.{HexString, Transactions}

object Merkle {
  // TODO Temporary dummy implementation
  def computeRoot(transactions: Transactions): HexString = SHA256.hash(transactions.toString.getBytes("UTF-8"))
}
