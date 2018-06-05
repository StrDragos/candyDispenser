package controllers

import dispenser.DispenserProtocol.{Inventory, InputResponse}
import play.api.libs.functional.syntax.unlift
import play.api.libs.functional.syntax._
import play.api.libs.json._

object JsonWriters {

  implicit val responseWriter: Writes[InputResponse] = (
    (JsPath \ "candies").write[Int] and
      (JsPath \ "coins").write[Int]
    ) (unlift(InputResponse.unapply))

  implicit val candyInventoryWriter: Writes[Inventory] =
    (JsPath \ "candies").write[Int].contramap(inv => inv.numberOfCandies)

}
