package controller

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import domain.ElectricityReading
import service.MeterReadingService




class MeterReadingController(meterReadingService: MeterReadingService) {
  def routes: Route = pathPrefix("readings") {
    get {
      path("read" / Segment) { smartMeterId =>
        complete(
          getReadings(smartMeterId)
        )
      }
    }
  }

  private def getReadings(smartMeterId: String): HttpResponse = {
    meterReadingService.getReadings(smartMeterId) match {
      case Some(readings) => HttpResponse(entity = jsonEntity(readings))
      case None => HttpResponse(status = StatusCodes.NotFound)
    }
  }

  private def jsonEntity(readings: List[ElectricityReading]): HttpEntity.Strict = {
    import io.circe.generic.auto._
    import io.circe.syntax._
    HttpEntity(MediaTypes.`application/json`, readings.asJson.noSpaces)
  }
}
