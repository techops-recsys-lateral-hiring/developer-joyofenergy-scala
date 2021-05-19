package com.tw.energy.domain

import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.generic.JsonCodec

import java.time.Instant
import squants.energy.{Kilowatts, Power}

case class ElectricityReading(time: Instant, reading: Power)

trait SquantsJsonSupport {
  implicit val encodePower: Encoder[Power] = (a: Power) => Json.obj(
    ("reading", Json.fromDouble(a.value).getOrElse(Json.Null)),
    ("unit", Json.fromString(a.unit.symbol))
  )

  implicit val decodePower: Decoder[Power] = (c: HCursor) => for {
    reading <- c.downField("reading").as[Double]
    unit <- c.downField("unit").as[String]
  } yield {
    Power((reading, unit)).get
  }

}
