package konradmalik.blockchain.api.routes

import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats, Serialization}

trait JsonSupport {
  implicit val formats: Formats = DefaultFormats
  implicit val naiveSerialization: Serialization = Serialization
}
