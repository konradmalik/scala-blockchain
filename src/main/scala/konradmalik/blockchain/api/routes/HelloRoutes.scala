package konradmalik.blockchain.api.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import konradmalik.blockchain.api.actors.Supervisor.{InitializePeerNetwork, InitializedPeerNetwork}
import org.json4s.native.Serialization.write


trait HelloRoutes extends RoutesSupport {

  def system: ActorSystem

  def supervisor: ActorRef

  lazy val helloRoutes: Route =
    path("initializeblockpool") {
      get {
        onSuccess(supervisor ? InitializePeerNetwork(2, 1)) {
          case i: InitializedPeerNetwork => complete((StatusCodes.OK, write(i)))
        }
      }
    }
}
