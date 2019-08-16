import Dependencies._

scalaVersion in ThisBuild := "2.12.9"
version in ThisBuild := "0.1.0"
organization in ThisBuild := "io.github.konradmalik"

lazy val root = (project in file("."))
  .settings(
    name := "Scala Akka Blockchain",
    libraryDependencies += sprayJson,
    libraryDependencies ++= akka,
    libraryDependencies ++= akkaHttp,
    libraryDependencies ++= scalaTest
  )


