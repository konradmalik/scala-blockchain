package io.github.konradmalik.blockchain

import akka.util.Timeout

import scala.concurrent.duration._

package object api {
  final val BLOCK_POOL_ACTOR_NAME = "blockPool"
  final val BLOCKCHAIN_ACTOR_NAME = "blockchain"
  final val PEER_ACTOR_NAME = "peer"
  final val TX_POOL_ACTOR_NAME = "txPool"

  final val PARENT_UP = "../"

  implicit val askTimeout: Timeout = Timeout(1 seconds)
  final val selectionTimeout: Duration = 5 seconds

  final case class ErrorMsg(timestmap: Long)
}
