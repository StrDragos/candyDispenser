package dispenser

object DispenserProtocol {
  sealed trait DispenserMessage
  case object InsertCoin extends DispenserMessage
  case object RotateKnob extends DispenserMessage
  case object GetInventory extends DispenserMessage

  case class Response(numberOfCandies:Int, insertedCoins: Int)
}

