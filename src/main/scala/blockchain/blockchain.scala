import java.nio.charset.Charset

import blockchain.core.Block

package object blockchain {

  final val DIFFICULTY: Int = 2

  //implicit val formats = DefaultFormats
  implicit val defaultCharset: Charset = Charset.forName("UTF-8")

  type Bytes = Array[Byte]
  type HexString = String
  type Chain = List[Block]

}
