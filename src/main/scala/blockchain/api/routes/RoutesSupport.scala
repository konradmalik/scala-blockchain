package blockchain.api.routes

import blockchain.json.JsonSupport

trait RoutesSupport extends JsonSupport {

}

sealed trait Answer
trait Success extends Answer
trait Failure extends Answer
