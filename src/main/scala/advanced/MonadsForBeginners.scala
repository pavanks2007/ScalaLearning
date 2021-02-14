package advanced

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MonadsForBeginners extends App {
  case class SafeValue[+T](private val internalValue: T) { // "constructor" = pure, or unit
    def get:T = synchronized {
      // does something interesting
      internalValue
    }

    def flatMap[S](transformer: T => SafeValue[S]): SafeValue[S] = synchronized { // bind, or flatMap
      transformer(internalValue)
    }
  }

  // external API
  def gimmeSafeValue[T](value:T): SafeValue[T] = SafeValue(value)

  val safeString: SafeValue[String] = gimmeSafeValue("Scala is awesome")
  // extract
  val string = safeString.get
  // transform
  val upperString = string.toUpperCase()
  // wrap
  val upperSafeString = SafeValue(upperString)

  // ETW
  // compressed
  val upperSafeString2 = safeString.flatMap(s => SafeValue(s.toUpperCase))

  // Assertion
  assert(upperSafeString == upperSafeString2)

  // Examples

  // Example 1: census
  case class Person(firstName:String, lastName:String) {
    assert(firstName != null && lastName != null)
  }

  // census API
  def getPerson(firstName:String, lastName:String): Person =
    if(firstName != null){
      if(firstName != null){
        Person(firstName, lastName)
      } else null
    } else null

  def getPersonBetter(firstName:String, lastName:String): Option[Person] =
    Option(firstName).flatMap{ fName =>
      Option(lastName).flatMap{ lName =>
        Option(Person(fName, lName))
      }
    }

  def getPersonFor(firstName:String, lastName:String): Option[Person] = for {
    fName <- Option(firstName)
    lName <- Option(lastName)
  } yield Person(fName, lName)

  // Example 2: asynchronous fetches

  case class User(id:String)
  case class Product(sku:String, price:Double)

  def getUser(url:String): Future[User] = Future {
    User("Pavan") // sample implementation
  }

  def getLastOrder(userId:String): Future[Product] = Future {
    Product("123-456", 99.99)
  }

  val pavanUrl = "my.store.com/users/pavan"

  /*
  getUser(pavanUrl).onComplete {
    case Success(User(id)) =>
      val lastOrder = getLastOrder(id)
      lastOrder.onComplete( {
        case Success(Product(sku, p)) =>
          val vatIncludedPrice = p * 1.18
          // pass it on - send email
      })
  }
  */

  val vatInclPrice: Future[Double] = getUser(pavanUrl)
    .flatMap(user => getLastOrder(user.id))
    .map(_.price * 1.18)

  val vatInclPriceFor = for {
    user <- getUser(pavanUrl)
    product <- getLastOrder(user.id)
  } yield product.price * 1.18

  // Example 3: double for-loops

  val numbers = List(1,2,3)
  val chars = List('a','b','c')
  // flatMaps
  val checkerboard: List[(Int, Char)] = numbers.flatMap(number => chars.map(char => (number, char)))
  // for
  val checkerboard2: List[(Int, Char)] = for {
    number <- numbers
    char <- chars
  } yield (number, char)

  // Properties

  // prop 1 - Left identity
  def twoConsecutive(x:Int) = List(x, x + 1)
  twoConsecutive(3) // List(3,4)
  List(3).flatMap(twoConsecutive) // List(3,4)
  // Monad(x).flatMap(f) == f(x)

  // prop 2 - Right identity
  List(1,2,3).flatMap(x => List(x)) // List(1,2,3)
  // Monad(v).flatMap(x => Monad(x)) USELESS, returns Monad(v)

  // prop 3 - Associativity (ETW-ETW)
  val incrementer = (x:Int) => List(x, x + 1)
  val doubler = (x:Int) => List(x, 2 * x)
  assert(
    numbers.flatMap(incrementer).flatMap(doubler) == numbers.flatMap(x => incrementer(x).flatMap(doubler))
  )
  /*
    List(
      incrementer(1).flatMap(doubler) -- 1,2,2,4
      incrementer(2).flatMap(doubler) -- 2,4,3,6
      incrementer(3).flatMap(doubler) -- 3,6,4,8
    )

    Monad(v).flatMap(f).flatMap(g) == Monad(v).flatMap(x => f(x).flatMap(g))
  */
  //
}
