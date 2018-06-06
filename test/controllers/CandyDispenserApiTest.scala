package controllers

import akka.actor.ActorSystem
import org.scalatestplus.play.PlaySpec

class CandyDispenserApiTest extends PlaySpec {

  "Api"  should {
    "return index" in {
      val controller = new CandyDispenserApi(ActorSystem("test"), )
    }
  }


}
