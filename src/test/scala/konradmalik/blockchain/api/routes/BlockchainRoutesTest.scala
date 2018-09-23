package konradmalik.blockchain.api.routes

import akka.actor.ActorRef
import akka.http.scaladsl.testkit.ScalatestRouteTest
import konradmalik.blockchain.api.actors.BlockchainActor
import org.scalatest.{FlatSpecLike, Matchers}
import spray.json.{JsArray, JsBoolean, JsObject, JsString}

class BlockchainRoutesTest extends FlatSpecLike with Matchers with ScalatestRouteTest with BlockchainRoutes  {

  val blockchain: ActorRef = system.actorOf(BlockchainActor.props(2), "blockchainActor")

  "Blockchain" should "return a json list of blocks and timestamp" in {
      Get("/blockchain/chain") ~> blockchainRoutes ~> check {
        val res: JsObject = responseAs[JsObject]
        assert(res.fields.keySet.contains("timestamp") && res.fields.keySet.contains("blockchain"))
        val blocksJson = res.getFields("blockchain").head.asInstanceOf[JsArray]
        assert(blocksJson.elements.size == 1)
      }
    }
  it should "return if it is valid" in {
    Get("/blockchain/valid") ~> blockchainRoutes ~> check {
      val res: JsObject = responseAs[JsObject]
      assert(res.fields.keySet.contains("timestamp") && res.fields.keySet.contains("valid"))
      val valid = res.getFields("valid").head.asInstanceOf[JsBoolean]
      assert(valid.value)
    }
  }
  it should "add and mine new block" in {
    Post("/blockchain/add_and_mine", "dummy_data") ~> blockchainRoutes ~> check {
      val res: JsObject = responseAs[JsObject]
      assert(res.fields.keySet.contains("timestamp") && res.fields.keySet.contains("block"))
      val block = res.getFields("block").head.asInstanceOf[JsObject]
      assert(block.getFields("data").head.asInstanceOf[JsString].value == "dummy_data")
    }
  }


}
