package konradmalik.blockchain.api

import akka.actor.ActorSystem
import konradmalik.blockchain.api.actors.Supervisor

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Server {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("blockchain-http-service")

    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    try {
      // top level supervisor
      val supervisor = system.actorOf(Supervisor.props(), "supervisor")
      // initialize required children
      supervisor ! Supervisor.InitializeBlockchainNetwork
      supervisor ! Supervisor.InitializePeerNetwork

      // stop after ENTER
      StdIn.readLine()
    } finally {
      system.terminate()
    }
  }

}
