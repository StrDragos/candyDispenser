package dispenser

object DispenserProtocol {

  case class InputResponse(numberOfCandies: Int, insertedCoins: Int)

  case class Inventory(numberOfCandies: Int)

  case object InsertCoin

  case object RotateKnob

  case object GetInventory

}

