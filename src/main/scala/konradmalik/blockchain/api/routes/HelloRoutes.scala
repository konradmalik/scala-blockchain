package konradmalik.blockchain.api.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import konradmalik.blockchain.api.Message
import konradmalik.blockchain.api.actors.Supervisor.InitializePeerNetwork

trait HelloRoutes extends RoutesSupport {

  def system: ActorSystem

  def supervisor: ActorRef

  lazy val helloRoutes: Route =
    path("hello") {
      get {
        onSuccess((supervisor ? InitializePeerNetwork(2, 1)).mapTo[Message]) {
          respondOnCreation
        }
      }
    }
}
