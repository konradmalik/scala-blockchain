package blockchain.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import blockchain.Chain
import blockchain.api.actors.BlockchainActor.ChainValidity
import blockchain.api.actors.BlockchainClusterListener.ChainRefreshed
import blockchain.core.Block
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  import spray.json._

  implicit val blockJsonWriter: JsonWriter[Block] = (b: Block) => JsObject(
    "index" -> JsNumber(b.index),
    "previousHash" -> JsString(b.previousHash),
    "data" -> JsString(b.data),
    "timestamp" -> JsNumber(b.timestamp),
    "nonce" -> JsNumber(b.nonce)
  )
  implicit val chainJsonWriter: JsonWriter[Chain] = (c: Chain) =>
    c.map(_.toJson).toJson

  implicit val chainValidityJsonWriter: JsonWriter[ChainValidity] = (c: ChainValidity) =>
    JsObject(
      "timestamp" -> JsString(c.timestamp.toString),
      "isChainValid" -> JsBoolean(c.isChainValid)
    )

  implicit val chainRefreshedJsonWriter: JsonWriter[ChainRefreshed] = (cr: ChainRefreshed) =>
    JsObject(
      "timestamp" -> JsString(cr.timestamp.toString),
      "newLength" -> JsNumber(cr.newLength)
    )

  implicit val blockJsonReader: JsonReader[Block] = (value: JsValue) => {
    value.asJsObject.getFields("index", "previousHash", "data", "timestamp", "nonce") match {
      case Seq(JsNumber(index), JsString(previousHash), JsString(data), JsNumber(timestamp), JsNumber(nonce)) =>
        new Block(index.toLongExact, previousHash, data, timestamp.toLongExact, nonce.toIntExact)
      case _ => throw DeserializationException("Bad block json format")
    }
  }

}
