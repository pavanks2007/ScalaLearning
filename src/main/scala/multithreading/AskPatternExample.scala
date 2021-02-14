package multithreading

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern._

import scala.concurrent.duration._
import akka.util.Timeout

//import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object AskPatternExample extends App{

  case object AskName
  case class AskNameOf(other:ActorRef)
  case class NameResponse(name:String)
  implicit val timeout = Timeout(1.seconds)

  class AskActor(val name:String) extends Actor {
    implicit val ec = context.system.dispatcher
    def receive: Receive = {
      case AskName =>
        sender ! NameResponse(name)
      case AskNameOf(other) =>
        val f = other ? AskName
        f.onComplete {
          case Success(NameResponse(n)) =>
            println("They said their name was "+n)
          case Success(s) =>
            println("They didn't tell us their name")
          case Failure(exception) => println("Asking their name failed")
        }
    }
  }

  val system = ActorSystem("AskPatternSystem")
  val actor = system.actorOf(Props(new AskActor("Pavan")),"AskActor1")
  val actor2 = system.actorOf(Props(new AskActor("Rohan")),"AskActor2")
  implicit val ec = system.dispatcher
//  1
//  val answer = actor ? AskName
//  answer.foreach(n => println(s"Name is $n"))

//  2
  actor ! AskNameOf(actor2)

  system.terminate()
}
