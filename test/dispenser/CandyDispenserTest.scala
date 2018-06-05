package dispenser

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit}
import dispenser.CandyDispenser.{Locked, Unlocked}
import dispenser.DispenserProtocol.{InsertCoin, Response, RotateKnob}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike}

import scala.concurrent.duration._

class CandyDispenserTest extends TestKit(ActorSystem("test"))
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  "Dispenser" must {
    val actor = TestFSMRef(new CandyDispenser(100))

    "have an initial state" in {
      actor.stateName shouldBe Locked
      actor.stateData.numberOfCandies should be > 0
    }

    "change state to unlocked and return coins" in {
      actor ! InsertCoin
      actor.stateName shouldBe Unlocked
      expectMsg(Response(100, 1))
    }

    "not change state or accumulate coins if in Unlocked" in {
      actor ! InsertCoin
      actor.stateName shouldBe Unlocked
      expectNoMessage()
    }

    "give candy and go to Locked state" in {
      actor ! RotateKnob
      actor.stateName shouldBe Locked
      expectMsg(Response(99, 1))
    }

    "not give candy if in locked state and the knob is turned" in {
      actor ! RotateKnob
      actor.stateName shouldBe Locked
      expectNoMessage()
    }
  }

  "When dispenser is refilled" must {
    "in locked sate and keep state" in {
      val actor = TestFSMRef(new CandyDispenser(1, 10 seconds), "refill-actor")
      actor ! InsertCoin
      actor ! RotateKnob
      awaitCond(actor.stateData.numberOfCandies == 1, 11 seconds , 5 seconds)
      actor.stateName shouldBe Locked
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
