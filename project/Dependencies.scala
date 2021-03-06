import sbt._

object Dependencies {
  lazy val akka = {
    val version = "2.5.24"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % version,
      "com.typesafe.akka" %% "akka-remote" % version,
      "com.typesafe.akka" %% "akka-cluster" % version,
      "com.typesafe.akka" %% "akka-cluster-metrics" % version,
      "com.typesafe.akka" %% "akka-cluster-tools" % version,
      "com.typesafe.akka" %% "akka-stream" % version,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % version % Test
    )
  }

  lazy val akkaHttp = {
    val version = "10.1.9"
    Seq(
      "com.typesafe.akka" %% "akka-http" % version,
      "com.typesafe.akka" %% "akka-http-spray-json" % version,
      "com.typesafe.akka" %% "akka-http-testkit" % version % Test
    )
  }

  lazy val sprayJson = "io.spray" %% "spray-json" % "1.3.5"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.7" % Test


}

