package controller

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import domain.MeterReadings
import domain.StringTypes.SmartMeterId
import io.circe.generic.auto._
import service.MeterReadingService


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
    meterReadingService.getReadings(smartMeterId) match {
      case Some(readings) => ToResponseMarshallable(readings)
      case None => ToResponseMarshallable(StatusCodes.NotFound)
    }
  }
}
