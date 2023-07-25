package com.tw.energy.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tw.energy.domain.{ElectricityReading, MeterReadings, PeakTimeMultiplier, PricePlan, PricePlanCosts}
import spray.json.{DefaultJsonProtocol, JsNumber, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

import java.time.{DayOfWeek, Instant}

trait JavaTimeAwareJsonProtocol extends DefaultJsonProtocol {
  implicit object DayOfWeekJsonFormat extends JsonFormat[DayOfWeek] {
    def write(dayOfWeek: DayOfWeek) = JsString(dayOfWeek.name())

    def read(json: JsValue): DayOfWeek = {
      json match {
        case JsString(dayOfWeekAsString) => DayOfWeek.valueOf(dayOfWeekAsString)
        case _ => deserializationError("String expected")
      }
    }
  }

  implicit object InstantFormat extends JsonFormat[Instant] {
    override def read(json: JsValue): Instant = {
      json match {
        case JsNumber(millis) => Instant.ofEpochMilli(millis.longValue)
        case _ => deserializationError("Long expected")
      }
    }

    override def write(instant: Instant): JsValue = JsNumber(instant.toEpochMilli)
  }
}

trait JsonSupport extends SprayJsonSupport with JavaTimeAwareJsonProtocol {
  implicit val electrictyReadingFormat:RootJsonFormat[ElectricityReading] = jsonFormat2(ElectricityReading.apply)
  implicit val meterReadingsFormat:RootJsonFormat[MeterReadings] = jsonFormat2(MeterReadings.apply)
  implicit val pricePlanFormat:RootJsonFormat[PricePlan] = jsonFormat4(PricePlan.apply)
  implicit val peakTimeMultiplierFormat: RootJsonFormat[PeakTimeMultiplier] = jsonFormat2(PeakTimeMultiplier.apply)
  implicit val pricePlanCosts: RootJsonFormat[PricePlanCosts] = jsonFormat2(PricePlanCosts.apply)
}
