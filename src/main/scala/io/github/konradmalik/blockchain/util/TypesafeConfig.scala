package io.github.konradmalik.blockchain.util

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory


trait TypesafeConfig {
  private val defaultConfig: Config = ConfigFactory.parseResources("defaults.conf")
  private val overridesConfig: Config = ConfigFactory.parseResources("overrides.conf")
    .withFallback(defaultConfig)
  // overwrite cluster hosts and ports to the ones defined in http service
  // Override the configuration of the port
  private val imputedConfig = ConfigFactory.parseString(s"""
        akka.remote.netty.tcp.hostname=${overridesConfig.getString("akka.http.host")}
        akka.remote.netty.tcp.port=${overridesConfig.getString("akka.http.port")}
        akka.remote.artery.canonical.port=${overridesConfig.getString("akka.http.host")}
        akka.remote.artery.canonical.port=${overridesConfig.getString("akka.http.port")}
        """).withFallback(overridesConfig)
  // now try to load the conf provided by user, if any
  val config: Config = ConfigFactory.load.withFallback(imputedConfig)
}
