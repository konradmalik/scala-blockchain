package konradmalik.blockchain.api.routes

import akka.util.Timeout

import scala.concurrent.duration._

trait RoutesSupport extends JsonSupport {
  // Required by the `ask` (?) method
  // usually we'd obtain the timeout from the system's configuration
  implicit lazy val timeout: Timeout = Timeout(5.seconds)
}

sealed trait Answer
trait Success extends Answer
trait Failure extends Answer
