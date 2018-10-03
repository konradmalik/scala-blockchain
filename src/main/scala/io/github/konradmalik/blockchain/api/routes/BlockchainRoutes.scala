package io.github.konradmalik.blockchain.api.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import io.github.konradmalik.blockchain.api._
import io.github.konradmalik.blockchain.api.actors.BlockchainActor.BlockMsg
import io.github.konradmalik.blockchain.api.actors.{BlockchainActor, BlockchainClusterListener}
import spray.json._

import scala.concurrent.Future

trait BlockchainRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val blockchainRoutes: Route =
    pathPrefix("blockchain") {
      concat(
        path("chain") {
          get {
            val chainFuture = blockchain ? BlockchainActor.GetChain(System.currentTimeMillis())
            onSuccess(chainFuture.mapTo[BlockchainActor.Chain]) { chain =>
              complete(StatusCodes.OK, chain.toJson)
            }
          }
        },
        path("valid") {
          get {
            val validFuture = blockchain ? BlockchainActor.IsChainValid(System.currentTimeMillis())
            onSuccess(validFuture.mapTo[BlockchainActor.ChainValidity]) { validity =>
              complete(StatusCodes.OK, validity.toJson)
            }
          }
        },
        path("last") {
          get {
            val blockFuture = blockchain ? BlockchainActor.GetLastBlock(System.currentTimeMillis())
            onSuccess(blockFuture.mapTo[BlockchainActor.BlockMsg]) { msg =>
              complete(StatusCodes.OK, msg.toJson)
            }
          }
        },
        path("refresh") {
          get {
            val newLength = (blockchainClusterListener ? BlockchainClusterListener.UpdateChain(System.currentTimeMillis())).mapTo[Int]
            onSuccess(newLength) { l =>
              complete(StatusCodes.OK, l.toString)
            }
          }
        },
        path("mine") {
          post {
            entity(as[String]) { entity =>
              val newBlock: Future[Any] = blockchain ? BlockchainActor.MakeNewBlock(System.currentTimeMillis(), entity)
              onSuccess(newBlock) {
                case ErrorMsg(_) => complete(StatusCodes.InternalServerError)
                case added: BlockMsg => complete(StatusCodes.Created, added.toJson)
              }
            }
          }
        }
      )
    }

  def blockchain: ActorRef

  def blockchainClusterListener: ActorRef
}
