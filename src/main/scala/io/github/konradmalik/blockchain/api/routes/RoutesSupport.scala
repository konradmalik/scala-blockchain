package io.github.konradmalik.blockchain.api.routes

trait RoutesSupport extends JsonSupport {

}

sealed trait Answer
trait Success extends Answer
trait Failure extends Answer
