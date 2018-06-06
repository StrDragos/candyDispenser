package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import controllers.JsonWriters._
import dispenser.DispenserProtocol._
import javax.inject.{Inject, Named, Singleton}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


@Singleton
class CandyDispenserApi @Inject()(@Named("candy-dispenser") candyDispenser: ActorRef, cc: ControllerComponents)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val timeout: Timeout = 5 seconds

  def inputCoin() = Action.async {
    (candyDispenser ? InsertCoin)
      .mapTo[InputResponse]
      .map(resp => Ok(Json.toJson(resp)))
  }

  def rotateKnob() = Action.async {
    (candyDispenser ? RotateKnob)
      .mapTo[InputResponse]
      .map(resp => Ok(Json.toJson(resp)))
  }

  def askInventory() = Action.async {
    (candyDispenser ? GetInventory)
      .mapTo[Inventory]
      .map(resp => Ok(Json.toJson(resp)))
  }
}
