package konradmalik.blockchain.api.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import konradmalik.blockchain.api.actors.BlockchainActor.Chain
import konradmalik.blockchain.core.{Block, Blockchain}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  import spray.json._

  /**
    * BLOCK
    */
  implicit val blockJsonWriter: JsonWriter[Block] = (b: Block) => JsObject(
    "index" -> JsNumber(b.index),
    "previousHash" -> JsString(b.previousHash),
    "data" -> JsString(b.data),
    "timestamp" -> JsNumber(b.timestamp),
    "nonce" -> JsNumber(b.nonce)
  )

  implicit val blockJsonReader: JsonReader[Block] = (value: JsValue) => {
    value.asJsObject.getFields("index", "previousHash", "data", "timestamp", "nonce") match {
      case Seq(JsNumber(index), JsString(previousHash), JsString(data), JsNumber(timestamp), JsNumber(nonce)) =>
        new Block(index.toLongExact, previousHash, data, timestamp.toLongExact, nonce.toIntExact)
      case _ => throw DeserializationException("Bad block json format")
    }
  }

  /**
    * Blockchain
    */
  implicit val blockchainJsonWriter: JsonWriter[Blockchain] = (b: Blockchain) =>
    JsArray(b.getBlockchain.toVector.map(_.toJson))

  /**
    * Chain
    */
  implicit val chainJsonWriter: JsonWriter[Chain] = (c: Chain) =>
    JsObject("timestamp" -> JsNumber(c.requestId), "blockchain" -> c.chain.toJson)
}