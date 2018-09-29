package io.github.konradmalik.blockchain.api

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import io.github.konradmalik.blockchain.api.actors.{BlockchainClusterListener, Supervisor}
import io.github.konradmalik.blockchain.api.routes.BlockchainRoutes
import io.github.konradmalik.blockchain.util.TypesafeConfig

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.io.StdIn

object Server extends App with TypesafeConfig with BlockchainRoutes {

  implicit val system: ActorSystem = ActorSystem("blockchain-http-service", config)
  // Create an actor that handles cluster domain events
  val blockchainClusterListener = system.actorOf(Props[BlockchainClusterListener], name = "blockchain-cluster-listener")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // top level supervisor
  val supervisor: ActorRef = system.actorOf(Supervisor.props(), "supervisor")
  // initialize required children
  val blockchain = Await.result(
    (supervisor ? Supervisor.InitializeBlockchain(System.currentTimeMillis()))
      .flatMap(_ => system.actorSelection("user/supervisor/" + BLOCKCHAIN_ACTOR_NAME)
        .resolveOne(FiniteDuration(selectionTimeout._1, selectionTimeout._2))), selectionTimeout
  )
  //supervisor ? Supervisor.InitializeBlockPoolNetwork(1, 1) // not yet useful/implemented
  //supervisor ? Supervisor.InitializePeerNetwork(2, 1) // not yet useful/implemented

  // rest api
  val routes: Route = blockchainRoutes

  val bindingFuture = Http().bindAndHandle(routes, config.getString("akka.http.host"), config.getInt("akka.http.port"))

  println(s"Server online at http://" + config.getString("akka.http.host") + ":" + config.getInt("akka.http.port") +
    "\nPress RETURN to stop...")

  // stop after ENTER
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
