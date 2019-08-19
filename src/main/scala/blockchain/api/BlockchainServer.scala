package blockchain.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import blockchain.api.actors.Supervisor.InitializedBlockchain
import blockchain.api.actors.{BlockchainClusterListener, Supervisor}
import blockchain.api.routes.{BlockchainClusterRoutes, BlockchainRoutes}
import blockchain.util.ServerConfig

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.io.StdIn

object BlockchainServer extends App with ServerConfig
  with BlockchainRoutes with BlockchainClusterRoutes {

  if (args.length < 3) {
    println("Please provide at least: hostname http-port akka-cluster-port")
    System.exit(1)
  } else {
    println(s"Provided args: ${args.mkString(";")}")
  }

  val AKKA_PROTOCOL = "akka.tcp"
  val ACTOR_SYSTEM_NAME = "blockchainSystem"
  val RUNNING_CLUSTER_ADDRESS: Option[String] = if (args.length > 3) Some(s"$AKKA_PROTOCOL://$ACTOR_SYSTEM_NAME@${args(3)}") else None

  override implicit val system: ActorSystem = ActorSystem(ACTOR_SYSTEM_NAME, config)
  // Create an actor that handles cluster domain events
  override val blockchainClusterListener: ActorRef = system.actorOf(BlockchainClusterListener.props(RUNNING_CLUSTER_ADDRESS), name = BLOCKCHAIN_CLUSTER_ACTOR_NAME)

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // top level supervisor
  val supervisor: ActorRef = system.actorOf(Supervisor.props, SUPERVISOR_ACTOR_NAME)
  // initialize required children
  override val blockchain = Await.result(
    (supervisor ? Supervisor.InitializeBlockchain)
      .mapTo[InitializedBlockchain].map(_.actor), selectionTimeout
  )
  //supervisor ? Supervisor.InitializeBlockPoolNetwork(1, 1) // not yet useful/implemented
  //supervisor ? Supervisor.InitializePeerNetwork(2, 1) // not yet useful/implemented

  // rest api
  val routes: Route = concat(blockchainRoutes, blockchainClusterRoutes)

  val bindingFuture = Http().bindAndHandle(routes, config.getString("akka.http.host"), config.getInt("akka.http.port"))

  println(s"Server online at http://" + config.getString("akka.http.host") + ":" + config.getInt("akka.http.port") +
    "\nPress RETURN to stop...")

  // stop after ENTER
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
