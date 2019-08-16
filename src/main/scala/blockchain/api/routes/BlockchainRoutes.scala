package blockchain.api.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import blockchain.api._
import blockchain.api.actors.{BlockchainActor, BlockchainClusterListener}
import blockchain.core.{Block, Blockchain}
import blockchain.json.JsonSupport
import spray.json._

import scala.concurrent.Future

trait BlockchainRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val blockchainRoutes: Route =
    pathPrefix("blockchain") {
      concat(
        path("chain") {
          get {
            val chainFuture = blockchain ? BlockchainActor.GetBlockchain
            onSuccess(chainFuture.mapTo[Blockchain]) { chain =>
              complete(StatusCodes.OK, chain.toJson)
            }
          }
        },
        path("valid") {
          get {
            val validFuture = blockchain ? BlockchainActor.IsChainValid
            onSuccess(validFuture.mapTo[Boolean]) { validity =>
              complete(StatusCodes.OK, validity.toJson)
            }
          }
        },
        path("last") {
          get {
            val blockFuture = blockchain ? BlockchainActor.GetLastBlock
            onSuccess(blockFuture.mapTo[Block]) { msg =>
              complete(StatusCodes.OK, msg.toJson)
            }
          }
        },
        path("refresh") {
          post {
            val newLength = (blockchainClusterListener ? BlockchainClusterListener.RefreshChain).mapTo[Int]
            onSuccess(newLength) { l =>
              complete(StatusCodes.OK, l.toString)
            }
          }
        },
        path("mine") {
          post {
            entity(as[String]) { entity =>
              val newBlock: Future[Any] = blockchain ? BlockchainActor.MakeNewBlock(entity)
              onSuccess(newBlock) {
                case ErrorMsg(_) => complete(StatusCodes.InternalServerError)
                case added: Block => complete(StatusCodes.Created, added.toJson)
              }
            }
          }
        }
      )
    }

  def blockchain: ActorRef

  def blockchainClusterListener: ActorRef
}
