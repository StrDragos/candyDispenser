package dispenser

object DispenserProtocol {
  sealed trait DispenserInput
  case object InsertCoin extends DispenserInput
  case object RotateKnob extends DispenserInput

  case class Response(numberOfCandies:Int, insertedCoins: Int)
}

