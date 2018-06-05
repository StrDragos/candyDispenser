package dispenser

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit}
import dispenser.CandyDispenser.{DispenserData, Locked, Unlocked}
import dispenser.DispenserProtocol.{InsertCoin, Response, RotateKnob}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpecLike}

import scala.concurrent.duration._

class CandyDispenserTest extends TestKit(ActorSystem("test"))
  with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  class TestContext(refillTime:FiniteDuration = 30 minutes){
    val actor = TestFSMRef(new CandyDispenser(refillTime))
  }

  "Dispenser" must {

    "be in lock" in new TestContext(){
      actor.stateName shouldBe Locked
    }

    "change state to unlocked and return coins" in new TestContext(){
      actor.setState(Locked, DispenserData(10, 0))
      actor ! InsertCoin
      actor.stateName shouldBe Unlocked
      expectMsg(Response(10, 1))
    }

    "not change state or accumulate coins if in Unlocked" in new TestContext() {
      actor.setState(Unlocked, DispenserData(10, 1))
      actor ! InsertCoin
      actor.stateName shouldBe Unlocked
      actor.stateData.coins shouldBe 1
      expectNoMessage()
    }

    "give candy and go to Locked state" in new TestContext(){
      actor.setState(Unlocked, DispenserData(10, 1))
      actor ! RotateKnob
      actor.stateName shouldBe Locked
      expectMsg(Response(9, 1))
    }

    "not give candy if in locked state and the knob is turned" in new TestContext() {
      actor ! RotateKnob
      actor.stateName shouldBe Locked
      expectNoMessage()
    }

    "not got to unlock if there are no candies" in new TestContext(){
      actor.setState(Locked, DispenserData(0, 1))
      actor ! InsertCoin
      actor.stateName shouldBe Locked
      expectNoMessage()
    }

    "refill after a period of time" in new TestContext(10 seconds){
      actor.setState(Locked, DispenserData(0, 1))
      awaitCond(actor.stateData.numberOfCandies > 0, 11 seconds , 5 seconds)
      actor.stateName shouldBe Locked
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
