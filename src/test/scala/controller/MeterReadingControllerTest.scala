package controller

import java.time.Instant

import akka.http.scaladsl.model.{MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import domain.ElectricityReading
import org.scalatest.{FlatSpec, Matchers}
import service.MeterReadingService

class MeterReadingControllerTest extends FlatSpec with Matchers with ScalatestRouteTest {
  "meterReadingController" should "respond with meterReadings for valid meterId" in {
    val time = "2019-01-24T18:11:27.142Z"
    val reading = 0.6
    val service = new MeterReadingService(Map("validId" -> List(ElectricityReading(Instant.parse(time), reading))))
    val controller = new MeterReadingController(service)

    Get("/readings/read/validId") ~> controller.routes ~> check {
      mediaType should be (MediaTypes.`application/json`)
      responseAs[String] should be (s"""[{"time":"$time","reading":$reading}]""")
    }
  }

  "meterReadingController" should "respond with NotFound for unknown meterId" in {
    val service = new MeterReadingService(Map())
    val controller = new MeterReadingController(service)

    Get("/readings/read/invalidId") ~> controller.routes ~> check {
      status should be (StatusCodes.NotFound)
    }
  }
}
