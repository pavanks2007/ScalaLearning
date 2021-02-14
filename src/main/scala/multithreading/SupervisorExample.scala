package multithreading

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props}

object SupervisorExample extends App {
  case object CreateChild
  case class SignalChildren(order:Int)
  case class PrintSignal(order:Int)
  case class DivideNumbers(n:Int, d:Int)
  case object BadStuff

  class ParentActor extends Actor {
    private var number = 0
    def receive: Receive = {
      case CreateChild =>
        context.actorOf(Props[ChildActor], "child"+number)
        number += 1
      case SignalChildren(n) =>
        context.children.foreach(_ ! PrintSignal(n))
    }

    override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
      case ae:ArithmeticException => Resume
      case _ => Restart
    }
  }

  class ChildActor extends Actor {
    println("Child created")
    def receive: Receive = {
      case PrintSignal(n) => println(n+" "+self.path)
      case DivideNumbers(n,d) => println(n/d)
      case BadStuff => throw new RuntimeException("Bad stuff happened")
    }
    override def preStart() = {
      super.preStart()
      println("preStart")
    }
    override def postStop() = {
      super.postStop()
      println("postStop")
    }
    override def preRestart(reason:Throwable, message:Option[Any]) = {
      super.preRestart(reason, message)
      println("preRestart")
    }
    override def postRestart(reason:Throwable) = {
      super.postRestart(reason)
      println("postRestart")
    }
  }

  val system = ActorSystem("HierarchyAdvancedSystem")
  val actor = system.actorOf(Props[ParentActor], "Parent1")
  val actor2 = system.actorOf(Props[ParentActor], "Parent2")

  actor ! CreateChild
  val child0 = system.actorSelection("/user/Parent1/child0")
  child0 ! DivideNumbers(4,0)
  child0 ! DivideNumbers(4,2)
  child0 ! BadStuff

  Thread.sleep(1000)
  system.terminate()
}
