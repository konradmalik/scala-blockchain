package blockchain.api.routes

import akka.actor.ActorRef
import akka.http.scaladsl.testkit.ScalatestRouteTest
import blockchain.api.actors.{BlockchainActor, BlockchainClusterListener}
import org.scalatest.{FlatSpecLike, Matchers}
import spray.json.{JsArray, JsObject, JsString}

class BlockchainRoutesTest extends FlatSpecLike with Matchers with ScalatestRouteTest with BlockchainRoutes {

  override val blockchainClusterListener: ActorRef = system.actorOf(BlockchainClusterListener.props, name = "blockchainClusterActor")
  override val blockchain: ActorRef = system.actorOf(BlockchainActor.props(2), "blockchainActor")

  "Blockchain" should "return a json list of blocks" in {
    Get("/blockchain/chain") ~> blockchainRoutes ~> check {
      val blocksJson = responseAs[JsArray]
      assert(blocksJson.elements.size == 1)
    }
  }
  it should "return if it is valid" in {
    Get("/blockchain/valid") ~> blockchainRoutes ~> check {
      responseAs[String] shouldEqual "true"
    }
  }
  it should "add and mine new block" in {
    Post("/blockchain/mine", "dummy_data") ~> blockchainRoutes ~> check {
      val res: JsObject = responseAs[JsObject]
      assert(res.getFields("data").head.asInstanceOf[JsString].value == "dummy_data")
    }
  }
  it should "get last block" in {
    Get("/blockchain/last") ~> blockchainRoutes ~> check {
      val res: JsObject = responseAs[JsObject]
      assert(res.getFields("data").head.asInstanceOf[JsString].value == "dummy_data")
    }
  }

}
