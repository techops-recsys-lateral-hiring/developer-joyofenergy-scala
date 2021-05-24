package com.tw.energy.controller

import java.time.Instant
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tw.energy.domain.ElectricityReading
import com.tw.energy.service.MeterReadingService
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import squants.energy.Kilowatts

class MeterReadingControllerTest extends AnyFlatSpec with Matchers with ScalatestRouteTest {
  val time = "2019-01-24T18:11:27.142Z"
  val reading = Kilowatts(0.6)
  val smartMeterId = "validId"
  val jsonElectricityReadings = s"""[{"time":"$time","reading":{"amount":0.6,"unit":"kW"}}]"""
  val jsonMeterReadings = s"""{"smartMeterId":"$smartMeterId","electricityReadings":$jsonElectricityReadings}"""



  "GET /readings/read/<meterId>" should "be answered with meterReadings for valid meterId" in {
    val service = new MeterReadingService(Map(smartMeterId -> List(ElectricityReading(Instant.parse(time), reading))))
    val controller = new MeterReadingController(service)

    Get("/readings/read/validId") ~> controller.routes ~> check {
      mediaType should be (MediaTypes.`application/json`)
      responseAs[String] should be (jsonElectricityReadings)
    }
  }

  "GET /readings/read/<meterId>" should "be answered with NotFound for unknown meterId" in {
    val service = new MeterReadingService(Map())
    val controller = new MeterReadingController(service)

    Get("/readings/read/invalidId") ~> controller.routes ~> check {
      status should be (StatusCodes.NotFound)
    }
  }

  "POST /readings/store" should "store readings" in {
    val service = new MeterReadingService(Map())
    val controller = new MeterReadingController(service)

    val request = Post("/readings/store", HttpEntity(MediaTypes.`application/json`, jsonMeterReadings))
    request ~> controller.routes ~> check {
      status should be (StatusCodes.OK)
    }

    Get(s"/readings/read/$smartMeterId") ~> controller.routes ~> check {
      mediaType should be (MediaTypes.`application/json`)
      responseAs[String] should be (jsonElectricityReadings)
    }
  }

  "POST /readings/store" should "fail if request is malformed" in {
    val service = new MeterReadingService(Map())
    val controller = new MeterReadingController(service)

    val jsonMeterReadings = s"""{"electricityReadings":$jsonElectricityReadings}"""

    val request = Post("/readings/store", HttpEntity(MediaTypes.`application/json`, jsonMeterReadings))
    val routeWithRejectionHandler = Route.seal(controller.routes)
    request ~> routeWithRejectionHandler ~> check {
      status should be (StatusCodes.BadRequest)
    }
  }

}
