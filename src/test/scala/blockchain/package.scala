package konradmalik

import konradmalik.blockchain.core.{Block, Transaction}

import scala.collection.mutable

package object blockchain {

  type Bytes = Array[Byte]
  type HexString = String
  type Tx = Transaction
  type Transactions = mutable.ListBuffer[Transaction]
  type Chain = mutable.ListBuffer[Block]

}
