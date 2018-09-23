package konradmalik.blockchain.api.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import konradmalik.blockchain.api._
import konradmalik.blockchain.api.actors.BlockchainActor
import spray.json._

import scala.concurrent.Future

trait BlockchainRoutes extends JsonSupport {

  implicit def system: ActorSystem

  def blockchain: ActorRef

  lazy val blockchainRoutes: Route =
    pathPrefix("blockchain") {
      concat(
        path("chain") {
          get {
            val chainFuture = blockchain ? BlockchainActor.GetChain(System.currentTimeMillis())
            onSuccess(chainFuture.mapTo[BlockchainActor.Chain]) { chain =>
              complete(chain.toJson)
            }
          }
        },
        path("valid") {
          get {
            val validFuture = blockchain ? BlockchainActor.IsChainValid(System.currentTimeMillis())
            onSuccess(validFuture.mapTo[BlockchainActor.ChainValidity]) { validity =>
              complete(validity.toJson)
            }
          }
        },
        path("add_and_mine") {
          post {
            entity(as[String]) { entity =>
              val newBlock: Future[Any] = blockchain ? BlockchainActor.MakeNewBlock(System.currentTimeMillis(), entity)
              onSuccess(newBlock) {
                case BlockchainActor.ErrorAddingBlock(_) => complete(StatusCodes.InternalServerError)
                case added: BlockchainActor.BlockAdded => complete(StatusCodes.Created, added.toJson)
              }
            }
          }
        }
      )
    }
}
