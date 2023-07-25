package com.tw.energy.controller

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tw.energy.domain.ElectricityReading
import com.tw.energy.service.MeterReadingService
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant

class MeterReadingControllerTest extends AnyFlatSpec with Matchers with ScalatestRouteTest with JsonSupport {
  val time = "2019-01-24T18:11:27.142Z"
  val reading = 0.6
  val smartMeterId = "validId"
  val jsonElectricityReadings: String = s"""[{"time":"$time","reading":$reading}]"""
  val jsonMeterReadings: String = s"""{"smartMeterId":"$smartMeterId","electricityReadings":$jsonElectricityReadings}"""



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
