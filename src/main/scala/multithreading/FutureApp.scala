package multithreading

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import io.StdIn._
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object FutureApp extends App {
//  println("This is first.")
//  val f = Future {
//    println("Printing in the future.")
//  }
//  Thread.sleep(10)
//  println("This is last.")

//  1. Block the program from exiting.
//  readLine()

//  2. Testing onComplete
//  val f2 = Future {
//    for(i <- 1 to 30) yield ParallelCollect.fib(i)
////    3. To get failure throw exception
////    throw new RuntimeException("Bad")
//  }

//  f2.onComplete {
//    case Success(value) => println(value)
//    case Failure(exception) => println("Something went wrong: "+exception)
//  }

  //  Thread.sleep(5000)

//  4. Testing Await.result
//  println(Await.result(f2, 5.seconds))

//  5. Testing Future methods
  val page1 = Future {
    "Google "+io.Source.fromURL("https://www.google.com").take(100).mkString
  }
  val page2 = Future {
    "Facebook "+io.Source.fromURL("https://www.facebook.com").take(100).mkString
  }
  val page3 = Future {
    "Youtube "+io.Source.fromURL("https://www.youtube.com").take(100).mkString
  }
  val pages = List(page1, page2, page3)

//  6. Testing Future.firstCompletedOf
//  val firstCompletedPage = Future.firstCompletedOf(pages)
//  firstCompletedPage.foreach(println)

//  7. Testing Future.sequence
  val allPages = Future.sequence(pages)
  allPages.foreach(println)

  Thread.sleep(5000)
}
