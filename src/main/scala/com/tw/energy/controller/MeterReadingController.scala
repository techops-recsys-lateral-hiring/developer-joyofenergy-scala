package com.tw.energy.controller

import akka.http.scaladsl.marshalling.{ToEntityMarshaller, ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import com.tw.energy.domain.{ElectricityReading, MeterReadings}
import com.tw.energy.domain.StringTypes.SmartMeterId
import com.tw.energy.service.MeterReadingService
import spray.json.RootJsonWriter

class MeterReadingController(meterReadingService: MeterReadingService) extends JsonSupport {
  def routes: Route = pathPrefix("readings") {
    get {
      path("read" / Segment) { smartMeterId =>
        complete(getReadings(smartMeterId))
      }
    } ~ post {
        path("store") {
          entity(as[MeterReadings]) { meterReadings =>
            complete(storeReadings(meterReadings))
          }
        }
      }
  }

  private def storeReadings(meterReadings: MeterReadings): ToResponseMarshallable = {
    meterReadingService.storeReadings(meterReadings)
    ToResponseMarshallable(StatusCodes.OK)
  }

  private def getReadings(smartMeterId: SmartMeterId): ToResponseMarshallable = {
    implicit val jsonMarshaller: ToEntityMarshaller[Seq[ElectricityReading]] = sprayJsonMarshaller(immSeqFormat)
    meterReadingService.getReadings(smartMeterId) match {
      case Some(readings) => ToResponseMarshallable(readings)
      case None => ToResponseMarshallable(StatusCodes.NotFound)
    }
  }
}
