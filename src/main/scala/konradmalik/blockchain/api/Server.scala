package konradmalik.blockchain.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import konradmalik.blockchain.api.actors.Supervisor

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Server {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("blockchain-http-service")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    // top level supervisor
    val supervisor = system.actorOf(Supervisor.props(), "supervisor")
    // initialize required children
    supervisor ! Supervisor.InitializeBlockchainNetwork(0,1)
    supervisor ! Supervisor.InitializeBlockPoolNetwork(1,1)
    supervisor ! Supervisor.InitializePeerNetwork(2,1)

    // rest api
    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

    // stop after ENTER
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
