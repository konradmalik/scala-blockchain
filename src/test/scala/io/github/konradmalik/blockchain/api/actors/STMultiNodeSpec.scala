package io.github.konradmalik.blockchain.api.actors

import java.util.Properties

import akka.remote.testkit.{MultiNodeSpec, MultiNodeSpecCallbacks}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.language.implicitConversions

/**
  * Hooks up MultiNodeSpec with ScalaTest
  */
trait STMultiNodeSpec extends MultiNodeSpecCallbacks
  with FlatSpecLike with Matchers with BeforeAndAfterAll {
  self: MultiNodeSpec ⇒

  override def beforeAll(): Unit = multiNodeSpecBeforeAll()

  override def afterAll(): Unit = multiNodeSpecAfterAll()

  // Might not be needed anymore if we find a nice way to tag all logging from a node
//  override implicit def convertToWordSpecStringWrapper(s: String): WordSpecStringWrapper = new WordSpecStringWrapper(s"$s (on node '${self.myself.name}', $getClass)")
}