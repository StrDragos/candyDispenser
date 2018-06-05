package controllers

import akka.actor.ActorSystem
import dispenser.DispenserProtocol.{InsertCoin, Response, RotateKnob}
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import akka.pattern.ask

import scala.concurrent.duration._
import akka.util.Timeout
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class CandyDispenser @Inject()(system: ActorSystem, cc: ControllerComponents) extends AbstractController(cc) {
  implicit val timeout:Timeout = 5 seconds
  implicit val responseWriter : Writes[Response]=(
    (JsPath \ "candies").write[Int] and
      (JsPath \ "coins").write[Int]
  )(unlift(Response.unapply))

  private val candyDispenser = system.actorOf(dispenser.CandyDispenser.props, dispenser.CandyDispenser.name)

  def inputCoin() = Action.async{
    (candyDispenser ? InsertCoin).mapTo[Response].map{
      resp:Response => Ok(Json.toJson(resp))
    }
  }

  def rotateKnob() = Action.async{
    (candyDispenser ? RotateKnob).mapTo[Response].map{
     resp:Response => Ok(Json.toJson(resp))
    }
  }
}
