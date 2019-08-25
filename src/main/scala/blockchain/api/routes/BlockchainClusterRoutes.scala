package blockchain.api.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import blockchain.api._
import blockchain.api.actors.BlockchainClusterListener
import blockchain.api.actors.BlockchainClusterListener.ChainRefreshed
import blockchain.json.JsonSupport

trait BlockchainClusterRoutes extends JsonSupport {

  implicit def system: ActorSystem

  import spray.json._

  lazy val blockchainClusterRoutes: Route =
    pathPrefix("blockchain") {
      concat(
        path("refresh") {
          post {
            val cr = (blockchainClusterListener ? BlockchainClusterListener.RefreshChain).mapTo[ChainRefreshed]
            onSuccess(cr) { cr =>
              complete(StatusCodes.OK, cr.toJson)
            }
          }
        }
      )
    }

  def blockchainClusterListener: ActorRef
}
