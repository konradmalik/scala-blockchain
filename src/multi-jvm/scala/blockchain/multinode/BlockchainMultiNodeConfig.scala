package blockchain.multinode

import akka.remote.testconductor.RoleName
import akka.remote.testkit.MultiNodeConfig

object BlockchainMultiNodeConfig extends MultiNodeConfig {
  val node1: RoleName = role("node1")
  val node2: RoleName = role("node2")
}