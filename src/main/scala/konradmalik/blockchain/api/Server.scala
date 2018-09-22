package konradmalik.blockchain.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import konradmalik.blockchain.api.actors.Supervisor
import konradmalik.blockchain.api.routes.HelloRoutes
import konradmalik.blockchain.util.TypesafeConfig
import akka.pattern.ask

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Server extends App with TypesafeConfig
  with HelloRoutes {

  implicit val system: ActorSystem = ActorSystem("blockchain-http-service")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // top level supervisor
  val supervisor: ActorRef = system.actorOf(Supervisor.props(), "supervisor")
  // initialize required children
  supervisor ? Supervisor.InitializeBlockchainNetwork(System.currentTimeMillis(), 1)
  //supervisor ? Supervisor.InitializeBlockPoolNetwork(1, 1) // not yet useful/implemented
  //supervisor ? Supervisor.InitializePeerNetwork(2, 1) // not yet useful/implemented

  // rest api
  val routes: Route = helloRoutes

  val bindingFuture = Http().bindAndHandle(routes, config.getString("http.host"), config.getInt("http.port"))

  println(s"Server online at http://" + config.getString("http.host") + ":" + config.getInt("http.port") + "\nPress RETURN to stop...")

  // stop after ENTER
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
