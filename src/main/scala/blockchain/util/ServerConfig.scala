package blockchain.util

import com.typesafe.config.{Config, ConfigFactory}

trait ServerConfig {
  protected def args: Array[String]

  // try to load the conf provided by user, if any
  private lazy val applicationConfig: Config = ConfigFactory.load
  // if host and port provided in args, override application.conf
  lazy val config: Config =
    if (args.length >= 3)
      ConfigFactory.parseString(
        s"""
        akka.http.host=${args.head}
        akka.http.port=${args(1)}
        akka.remote.netty.tcp.hostname=${args.head}
        akka.remote.netty.tcp.port=${args(2)}
        """).withFallback(applicationConfig)
    else
      applicationConfig
}
