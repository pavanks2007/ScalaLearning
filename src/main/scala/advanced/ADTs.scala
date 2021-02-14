package advanced

object ADTs {

  // way of structuring your data

  // SUM types
  sealed trait Weather
  case object Sunny extends Weather
  case object Windy extends Weather
  case object Rainy extends Weather
  case object Cloudy extends Weather

  // Weather = Sunny || Windy|| Rainy || Cloudy
  // Weather = Sunny + Windy + Rainy + Cloudy == SUM type

  def feeling(weather:Weather):String = weather match {
    case Sunny => ":)"
    case Windy => ":|"
    case Rainy =>":("
    case Cloudy =>":*("
  }

  // PRODUCT types
  case class WeatherForecastRequest(latitude: Double, longitude: Double)
  // (Double, Double) => WFR
  // type WFR = Double x Double = P roduct Type

  // Hybrid Types
  sealed trait WeatherForecastResponse // SUM type
  case class Valid(weather: Weather) extends WeatherForecastResponse // PRODUCT type
  case class Invalid(error:String, description: String) extends WeatherForecastResponse // PRODUCT type

  // Advantages
  // 1. Illegal states are NOT representable
  // 2. Highly composable
  // 3. Immutable data structures
  // 4. Just data, not functionality => structure our code


  type NaiveWeather = String

  def naiveFeeling(weather:String) = weather match {
    case "sunny" => ":)"
    // other cases
  }

  naiveFeeling("45 degrees")
}
