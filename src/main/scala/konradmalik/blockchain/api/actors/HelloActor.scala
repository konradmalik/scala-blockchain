package konradmalik.blockchain.api.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import konradmalik.blockchain.api.actors.HelloActor.Message

object HelloActor {
  def props() = Props(new HelloActor)

  final case class Message(msgId: Long, msgContent: String)
}

class HelloActor extends Actor with ActorLogging {
  def receive: Receive = {
    case Message(id,content) => println(s"I received message $id containing $content")
    case _       => println("huh?")
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  // default Actor constructor
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
  helloActor ! HelloActor.Message(0,"hello")
  helloActor ! HelloActor.Message(1,"buenos dias")
  helloActor ! "wilkommen"
}