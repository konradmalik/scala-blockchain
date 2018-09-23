package konradmalik.blockchain.api.routes

import akka.actor.{ActorSelection, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import konradmalik.blockchain.api._
import konradmalik.blockchain.api.actors.BlockchainActor
import spray.json.DefaultJsonProtocol._
import spray.json._

trait BlockchainRoutes extends JsonSupport {

  implicit def system: ActorSystem

  def blockchain: ActorSelection

  lazy val blockchainRoutes: Route =
    pathPrefix("blockchain") {
      path("chain") {
        get {
          val chainFuture = blockchain ? BlockchainActor.GetChain(System.currentTimeMillis())
          onSuccess(chainFuture.mapTo[BlockchainActor.Chain]) { chain =>
            complete(chain.toJson)
          }
        }
      }
    }
}
