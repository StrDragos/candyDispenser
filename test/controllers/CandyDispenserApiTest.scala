package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

class CandyDispenserApiTest extends PlaySpec with GuiceOneAppPerSuite {

  "Api" should {
    "get candy inventory" in {
      route(app, FakeRequest(GET, "/dispenser/inventory")).map(status) mustBe Some(OK)
    }
  }


}
