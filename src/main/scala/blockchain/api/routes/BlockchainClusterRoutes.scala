package blockchain.api.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import blockchain.api._
import blockchain.api.actors.BlockchainClusterListener
import blockchain.json.JsonSupport

trait BlockchainClusterRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val blockchainClusterRoutes: Route =
    pathPrefix("blockchain") {
      concat(
        path("refresh") {
          post {
            val newLength = (blockchainClusterListener ? BlockchainClusterListener.RefreshChain).mapTo[Int]
            onSuccess(newLength) { l =>
              complete(StatusCodes.OK, l.toString)
            }
          }
        }
      )
    }

  def blockchainClusterListener: ActorRef
}
