package dispenser

import akka.actor.{FSM, Props}
import dispenser.CandyDispenser._
import dispenser.DispenserProtocol.{InsertCoin, Response, RotateKnob}

import scala.concurrent.duration._
import scala.util.Random

object CandyDispenser {
  val name = "candy-dispenser"

  def props: Props = Props(new CandyDispenser(Random.nextInt(Integer.MAX_VALUE)))

  sealed trait DispenserState

  case object Locked extends DispenserState

  case object Unlocked extends DispenserState

  private[dispenser] case class DispenserData(numberOfCandies: Int, coins: Int = 0)

}

private[dispenser] class CandyDispenser(initialLoad: Int, refillTime: FiniteDuration = 30 minutes) extends FSM[DispenserState, DispenserData] {


  startWith(Locked, DispenserData(initialLoad))

  when(Locked, refillTime) {
    case Event(InsertCoin, data@DispenserData(candies, insertedCoins)) if candies > 0 =>
      val newState = data.copy(coins = insertedCoins + 1)
      sender ! Response(candies, newState.coins)
      goto(Unlocked) using newState

    case Event(StateTimeout, data) =>
      stay using data.copy(numberOfCandies = initialLoad)

  }

  when(Unlocked, refillTime) {
    case Event(RotateKnob, data@DispenserData(candies, coins)) if candies > 0 =>
      val newState = data.copy(numberOfCandies = candies - 1)
      sender() ! Response(newState.numberOfCandies, coins)
      goto(Locked) using newState

    case Event(StateTimeout, data) =>
      stay using data.copy(numberOfCandies = initialLoad)
  }

  initialize()
}
