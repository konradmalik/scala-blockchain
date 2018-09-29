package io.github.konradmalik.blockchain.util

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory


trait TypesafeConfig {
  private val defaultConfig: Config = ConfigFactory.parseResources("defaults.conf")
  private val overridesConfig: Config = ConfigFactory.parseResources("overrides.conf")
    .withFallback(defaultConfig)
  // now try to load the conf provided by user, if any
  private val applicationConfig: Config = ConfigFactory.load.withFallback(overridesConfig)
  // overwrite cluster hosts and ports to the ones defined in http service
  // Override the configuration of the port
  val config: Config = ConfigFactory.parseString(s"""
        akka.remote.netty.tcp.hostname=${applicationConfig.getString("akka.http.host")}
        akka.remote.netty.tcp.port=${applicationConfig.getString("akka.http.port")}
        akka.remote.artery.canonical.port=${applicationConfig.getString("akka.http.host")}
        akka.remote.artery.canonical.port=${applicationConfig.getString("akka.http.port")}
        """).withFallback(applicationConfig)
}
