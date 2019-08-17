import Dependencies._
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings

scalaVersion in ThisBuild := "2.12.9"
version in ThisBuild := "0.1.0"
organization in ThisBuild := "io.github.konradmalik"

lazy val root = project
  .in(file("."))
  .settings(multiJvmSettings: _*)
  .settings(
    name := "Scala Akka Blockchain",
    libraryDependencies += sprayJson,
    libraryDependencies ++= akka,
    libraryDependencies ++= akkaHttp,
    libraryDependencies += scalaTest,
    // disable parallel tests
    parallelExecution in Test := false
  )
  .configs(MultiJvm)
