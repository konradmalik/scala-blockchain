package blockchain.multinode

import akka.remote.testconductor.RoleName
import akka.remote.testkit.MultiNodeConfig
import com.typesafe.config.ConfigFactory

object BlockchainMultiNodeConfig extends MultiNodeConfig {
  val node1: RoleName = role("node1")
  val node2: RoleName = role("node2")
  commonConfig(ConfigFactory.parseString("akka.actor.provider = cluster"))
}