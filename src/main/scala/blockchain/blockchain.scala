import java.nio.charset.Charset

import blockchain.core.Block

import scala.collection.mutable

package object blockchain {

  final val DIFFICULTY: Int = 2

  //implicit val formats = DefaultFormats
  implicit val defaultCharset: Charset = Charset.forName("UTF-8")

  type Bytes = Array[Byte]
  type HexString = String
  type Chain = mutable.ListBuffer[Block]

}
