package konradmalik.blockchain.util

import konradmalik.blockchain.core.Block
import org.json4s.JsonAST._
import org.json4s.{CustomSerializer, Extraction}
import konradmalik.blockchain._


class BlockSerializer extends CustomSerializer[Block](format => ( {
  case JObject(JField("index", JLong(index)) :: JField("previousHash", JString(previousHash)) :: JField("hash", _) ::
    JField("data", JString(data)) :: JField("merkleHash", JString(merkleHash)) ::
    JField("timestamp", JLong(timestamp)) :: JField("nonce", JInt(nonce)) :: JField("transactions", _) :: Nil) =>
    Block(index.longValue, previousHash, data, timestamp.longValue, nonce.intValue)
}, {
  case x: Block =>
    JObject(JField("index", JLong(x.index)) :: JField("previousHash", JString(x.previousHash)) ::  JField("hash", JString(x.hash)) ::
      JField("data", JString(x.data)) :: JField("merkleHash", JString(x.merkleHash)) ::
      JField("timestamp", JLong(x.timestamp)) :: JField("nonce", JInt(x.nonce)) :: JField("transactions", Extraction.decompose(x.transactions)) :: Nil)
}
))
