package dispenser

import akka.actor.{FSM, Props}
import dispenser.CandyDispenser._
import dispenser.DispenserProtocol._

import scala.concurrent.duration._
import scala.util.Random

object CandyDispenser {
  val name = "candy-dispenser"

  def props: Props = Props(new CandyDispenser())

  sealed trait DispenserState

  private[dispenser] case class DispenserData(numberOfCandies: Int, coins: Int = 0)

  case object Locked extends DispenserState

  case object Unlocked extends DispenserState

}
class CandyDispenser(refillTime: FiniteDuration = 30 minutes) extends FSM[DispenserState, DispenserData] {

  private lazy val initialLoad = Random.nextInt(Integer.MAX_VALUE)

  startWith(Locked, DispenserData(initialLoad))

  when(Locked, refillTime) {
    case Event(InsertCoin, data@DispenserData(candies, insertedCoins)) if candies > 0 =>
      val newState = data.copy(coins = insertedCoins + 1)
      sender ! InputResponse(candies, newState.coins)
      goto(Unlocked) using newState
  }

  when(Unlocked, refillTime) {
    case Event(RotateKnob, data@DispenserData(candies, coins)) if candies > 0 =>
      val newState = data.copy(numberOfCandies = candies - 1)
      sender ! InputResponse(newState.numberOfCandies, coins)
      goto(Locked) using newState
  }

  whenUnhandled {
    case Event(GetInventory, data) =>
      sender ! Inventory(data.numberOfCandies)
      stay

    case Event(StateTimeout, data) => stay using data.copy(numberOfCandies = initialLoad)

  }

  initialize()
}
