package com.tw.energy.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.tw.energy.domain.{ElectricityReading, MeterReadings, PeakTimeMultiplier, PricePlan, PricePlanCosts}
import spray.json.{DefaultJsonProtocol, JsNumber, JsObject, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

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
        case JsNumber(seconds) => Instant.ofEpochSecond(seconds.longValue)
        case _ => deserializationError("Long expected")
      }
    }

    override def write(instant: Instant): JsValue = JsNumber(instant.getEpochSecond)
  }
}

trait JsonSupport extends SprayJsonSupport with JavaTimeAwareJsonProtocol {
//  implicit object ElectricityReadingJsonFormat extends RootJsonFormat[ElectricityReading] {
//    override def read(json: JsValue): ElectricityReading = {
//      json.asJsObject.getFields("time", "reading") match {
//        case Seq(JsNumber(time), JsNumber(reading)) =>
//          new ElectricityReading(Instant.ofEpochSecond(time.longValue), reading)
//        case _ => deserializationError("ElectricityReading expected")
//      }
//    }
//
//    override def write(electricityReading: ElectricityReading): JsValue = {
//      JsObject(
//        "time" -> JsNumber(electricityReading.time.getEpochSecond),
//        "reading" -> JsNumber(electricityReading.reading)
//      )
//    }
//  }
  implicit val electricityReadingFormat: RootJsonFormat[ElectricityReading] = jsonFormat2(ElectricityReading.apply)
  implicit val meterReadingsFormat:RootJsonFormat[MeterReadings] = jsonFormat2(MeterReadings.apply)
  implicit val pricePlanFormat:RootJsonFormat[PricePlan] = jsonFormat4(PricePlan.apply)
  implicit val peakTimeMultiplierFormat: RootJsonFormat[PeakTimeMultiplier] = jsonFormat2(PeakTimeMultiplier.apply)
  implicit val pricePlanCosts: RootJsonFormat[PricePlanCosts] = jsonFormat2(PricePlanCosts.apply)
}
