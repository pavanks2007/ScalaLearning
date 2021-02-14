package multithreading

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.duration.DurationInt

object SchedulerExample extends App {
  case object Count

  class ScheduleActor extends Actor {
    var n = 0
    def receive: Receive = {
      case Count =>
        n += 1
        println(n)
    }
  }

  val system = ActorSystem("ScheduleSystem")
  val actor = system.actorOf(Props[ScheduleActor],"Actor")
  implicit val ec = system.dispatcher

  actor ! Count

  system.scheduler.scheduleOnce(1.second)(actor ! Count)

  val can = system.scheduler.scheduleWithFixedDelay(0.seconds, 100.millis, actor, Count)

  Thread.sleep(3000)
  can.cancel()
  system.terminate()
}
