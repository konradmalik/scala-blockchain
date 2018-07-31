package konradmalik.blockchain.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import akka.util.Timeout
import konradmalik.blockchain.api.{FailureMsg, Message, SuccessMsg}

import scala.concurrent.duration._

trait RoutesSupport extends JsonSupport {
  // Required by the `ask` (?) method
  // usually we'd obtain the timeout from the system's configuration
  implicit lazy val timeout: Timeout = Timeout(5.seconds)

  def respondOnCreation(m: Message): StandardRoute = m match {
    case s: SuccessMsg => complete((StatusCodes.Created, s.message))
    case f: FailureMsg => complete((StatusCodes.Conflict, f.error))
  }

  def respondOnDeletion(m: Message): StandardRoute = m match {
    case s: SuccessMsg => complete((StatusCodes.OK, s.message))
    case f: FailureMsg => complete((StatusCodes.NotFound, f.error))
  }

  def respondOnUpdate(m: Message): StandardRoute = m match {
    case s: SuccessMsg => complete((StatusCodes.OK, s.message))
    case f: FailureMsg => complete((StatusCodes.InternalServerError, f.error))
  }
}
