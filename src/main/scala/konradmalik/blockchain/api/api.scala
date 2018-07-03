package konradmalik.blockchain

import akka.util.Timeout

import scala.concurrent.duration._

package object api {
  final val BLOCK_POOL_NETWORK_ACTOR_NAME = "blockPool-network"
  final val BLOCK_POOL_ACTOR_NAME = "blockPool"
  final val BLOCKCHAIN_NETWORK_ACTOR_NAME = "blockchain-network"
  final val BLOCKCHAIN_ACTOR_NAME = "blockchain"
  final val PEER_NETWORK_ACTOR_NAME = "peer-network"
  final val PEER_ACTOR_NAME = "peer"
  final val TX_POOL_ACTOR_NAME = "txPool"

  final val PARENT_UP = "../"

  final val DIFFICULTY: Int = 2

  implicit val timeout: Timeout = Timeout(1 seconds)

}
