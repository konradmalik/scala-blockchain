package io.github.konradmalik.blockchain.api.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import io.github.konradmalik.blockchain.api.actors.BlockchainActor.{BlockMsg, Chain, ChainValidity}
import io.github.konradmalik.blockchain.core.{Block, Blockchain}
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
    JsObject("timestamp" -> JsNumber(c.timestamp), "blockchain" -> c.chain.toJson)

  /**
    * Chain validity
    */
  implicit val chainValidityJsonFormat: RootJsonFormat[ChainValidity] = jsonFormat2(ChainValidity)

  /**
    * BlockAdded
    */
  implicit val blockAddedJsonWriter: JsonWriter[BlockMsg] = (ba: BlockMsg) =>
    JsObject("timestamp" -> JsNumber(ba.timestamp), "block" -> ba.block.toJson)
}