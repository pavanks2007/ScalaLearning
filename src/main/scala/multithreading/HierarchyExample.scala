package multithreading

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object HierarchyExample extends App {
  case object CreateChild
  case object SignalChildren
  case object PrintSignal

  class ParentActor extends Actor {
    private var number = 0
    private val children = collection.mutable.Buffer[ActorRef]()
    def receive: Receive = {
      case CreateChild =>
        children += context.actorOf(Props[ChildActor], "child"+number)
        number += 1
      case SignalChildren =>
        children.foreach(_ ! PrintSignal)
    }
  }

  class ChildActor extends Actor {
    def receive: Receive = {
      case PrintSignal => println(self)
    }
  }

  val system = ActorSystem("HierarchySystem")
  val actor = system.actorOf(Props[ParentActor], "Parent1")

  actor ! CreateChild
  actor ! SignalChildren
  actor ! CreateChild
  actor ! CreateChild
  actor ! SignalChildren

  system.terminate()
}
