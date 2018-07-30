package konradmalik.blockchain.util

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory


trait TypesafeConfig {
  private val defaultConfig: Config = ConfigFactory.parseResources("defaults.conf")
  private val overridesConfig: Config = ConfigFactory.parseResources("overrides.conf")
    .withFallback(defaultConfig)
  // now try to load the conf provided by user, if any
  val config: Config = ConfigFactory.load.withFallback(overridesConfig)
}
