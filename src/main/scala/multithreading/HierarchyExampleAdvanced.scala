package multithreading

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object HierarchyExampleAdvanced extends App {
  case object CreateChild
  case class SignalChildren(order:Int)
  case class PrintSignal(order:Int)

  class ParentActor extends Actor {
    private var number = 0
    def receive: Receive = {
      case CreateChild =>
        context.actorOf(Props[ChildActor], "child"+number)
        number += 1
      case SignalChildren(n) =>
        context.children.foreach(_ ! PrintSignal(n))
    }
  }

  class ChildActor extends Actor {
    def receive: Receive = {
      case PrintSignal(n) => println(n+" "+self.path)
    }
  }

  val system = ActorSystem("HierarchyAdvancedSystem")
  val actor = system.actorOf(Props[ParentActor], "Parent1")
  val actor2 = system.actorOf(Props[ParentActor], "Parent2")

  actor ! CreateChild
  actor ! SignalChildren(1)
  actor ! CreateChild
  actor ! CreateChild
  actor ! SignalChildren(2)

  actor2 ! CreateChild
  val child0 = system.actorSelection("/user/Parent2/child0")
  child0 ! PrintSignal(3)

  Thread.sleep(1000)
  system.terminate()
}
