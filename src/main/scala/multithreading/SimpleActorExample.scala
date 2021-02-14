package multithreading

import akka.actor.{Actor, ActorSystem, Props}

object SimpleActorExample extends App {
  class SimpleActor extends Actor {
    def receive: Receive = {
      case s:String => println("String: "+s)
      case i:Int => println("Number: "+i)
      case _ => println("Unknown")
    }

    def foo: Unit = println("Normal methods")
  }

  val system = ActorSystem("SimpleSystem")
  val actor = system.actorOf(Props[SimpleActor],"SimpleActor")



  println("Before messages")
  actor ! "Hi there."
  println("After string")
  actor ! 42
  println("After int")
  actor ! 'a'
  println("After char")

}
