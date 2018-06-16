package konradmalik

import java.nio.charset.Charset

import konradmalik.blockchain.core.{Block, Transaction}
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats, NoTypeHints}

import scala.collection.mutable

package object blockchain {

  implicit val formats = DefaultFormats
  //implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)
  implicit val defaultCharset: Charset = Charset.forName("UTF-8")

  type Bytes = Array[Byte]
  type HexString = String
  type Transactions = mutable.ListBuffer[Transaction]
  type Chain = mutable.ListBuffer[Block]

}
