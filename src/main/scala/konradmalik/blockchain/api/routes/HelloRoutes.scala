package konradmalik.blockchain.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route

sealed trait Message

final case class SuccessMsg(message: String) extends Message

final case class FailureMsg(error: String) extends Message

trait HelloRoutes extends JsonSupport {

  lazy val helloRoutes: Route =
    path("hello") {
      get {
        complete(StatusCodes.OK, SuccessMsg("hello").message)
      }
    }
}
