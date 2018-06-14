package konradmalik.blockchain.core

import konradmalik.blockchain.{HexString, Transactions}

class Block(header: BlockHeader, transactions: Transactions)
  extends BlockHeader(header.index, header.previousHash, header.data, header.merkleHash, header.timestamp, header.nonce) with Serializable {

}

object Block {
  def mine(pow: ProofOfWork) = ??? // use proof of work to calculate hash by chaning nonce

  def apply(index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int, transactions: Transactions): Block =
    new Block(BlockHeader(index, previousHash, data, Merkle.computeRoot(transactions), timestamp, nonce), transactions)

  def apply(index: Long, previousHash: HexString, data: String, timestamp: Long, nonce: Int): Block ={
    val txs = new Transactions
    new Block(BlockHeader(index, previousHash, data, Merkle.computeRoot(txs), timestamp, nonce), txs)
  }

  def apply(index: Long, previousHash: HexString, data: String, nonce: Int, transactions: Transactions): Block =
    new Block(BlockHeader(index, previousHash, data: String, Merkle.computeRoot(transactions), nonce: Int), transactions)

  def apply(index: Long, previousHash: HexString, data: String, nonce: Int): Block = {
    val txs = new Transactions
    new Block(BlockHeader(index, previousHash, data, Merkle.computeRoot(txs), nonce), txs)
  }
}