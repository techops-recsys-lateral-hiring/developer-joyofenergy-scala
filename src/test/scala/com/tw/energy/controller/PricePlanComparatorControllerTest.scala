package com.tw.energy.controller

import java.time.Instant
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tw.energy.domain.{ElectricityReading, MeterReadings, PricePlan, PricePlanCosts, SquantsJsonSupport}
import com.tw.energy.service.{AccountService, MeterReadingService, PricePlanService}
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import squants.energy.{KilowattHours, Kilowatts}
import squants.market.{EUR, Money}

class PricePlanComparatorControllerTest extends AnyFlatSpec with Matchers with ScalatestRouteTest with SquantsJsonSupport {
  val pricePlan1Id = "test-supplier"
  val pricePlan2Id = "best-supplier"
  val pricePlan3Id = "second-best-supplier"
  val smartMeterId = "smart-meter-id"

  val pricePlan1 = PricePlan(pricePlan1Id, null, EUR(10)/KilowattHours(1), null)
  val pricePlan2 = PricePlan(pricePlan2Id, null, EUR(1)/KilowattHours(1), null)
  val pricePlan3 = PricePlan(pricePlan3Id, null, EUR(2)/KilowattHours(1), null)

  trait Setup {
    val meterReadingService = new MeterReadingService()
    val pricePlanService = new PricePlanService(List(pricePlan1, pricePlan2, pricePlan3), meterReadingService)
    val accountService = new AccountService(Map(smartMeterId -> pricePlan1Id))
    val controller = new PricePlanComparatorController(pricePlanService, accountService)
  }


  "GET /price-plans/compare-all/<meterId>" should "compare price plans" in new Setup {
    val electricityReading = ElectricityReading(Instant.now().minusSeconds(3600), Kilowatts(15))
    val otherReading = ElectricityReading(Instant.now(), Kilowatts(5))
    meterReadingService.storeReadings(MeterReadings(smartMeterId, List(electricityReading, otherReading)))

    val expectedPricePlanCost: Map[String, Money] = Map(
      pricePlan1Id -> EUR(BigDecimal("100.00")),
      pricePlan2Id -> EUR(BigDecimal("10.00")),
      pricePlan3Id -> EUR(BigDecimal("20.00")),
    )

    val expected = PricePlanCosts(Some(pricePlan1Id), expectedPricePlanCost)

    Get(s"/price-plans/compare-all/$smartMeterId") ~> controller.routes ~> check {
      responseAs[String] should be (expected.asJson.noSpaces)
    }
  }

  "GET /price-plans/recommend/<meterId>" should "recommend cheapest price plan" in new Setup {
    val electricityReading = ElectricityReading(Instant.now().minusSeconds(1800), Kilowatts(35))
    val otherReading = ElectricityReading(Instant.now(), Kilowatts(3))
    meterReadingService.storeReadings(MeterReadings(smartMeterId, List(electricityReading, otherReading)))

    val expectedPricePlanCost: List[Map[String, Money]] = List(
      Map(pricePlan2Id -> EUR(BigDecimal("9.50"))),
      Map(pricePlan3Id -> EUR(BigDecimal("19.00"))),
      Map(pricePlan1Id -> EUR(BigDecimal("95.00")))
    )

    Get(s"/price-plans/recommend/$smartMeterId") ~> controller.routes ~> check {
      responseAs[String] should be (expectedPricePlanCost.asJson.noSpaces)
    }
  }

  "GET /price-plans/recommend/<meterId>" should "recommend limited cheapest price plan" in new Setup {
    val electricityReading = ElectricityReading(Instant.now().minusSeconds(2700), Kilowatts(5))
    val otherReading = ElectricityReading(Instant.now(), Kilowatts(20))
    meterReadingService.storeReadings(MeterReadings(smartMeterId, List(electricityReading, otherReading)))

    val limit = 2
    val expectedPricePlanCost: List[Map[String, Money]] = List(
      Map(pricePlan2Id -> EUR(BigDecimal("9.3750"))),
      //TODO: how to properly handle trailing zeros in decoding/encoding?
      Map(pricePlan3Id -> EUR(BigDecimal("18.7500")))
    )

    Get(s"/price-plans/recommend/$smartMeterId?limit=$limit") ~> controller.routes ~> check {
      responseAs[String] should be (expectedPricePlanCost.asJson.noSpaces)
    }
  }

  "GET /price-plans/recommend/<meterId>" should "recommend all price plans if limit is high" in new Setup {
    val electricityReading = ElectricityReading(Instant.now().minusSeconds(3600), Kilowatts(25))
    val otherReading = ElectricityReading(Instant.now(), Kilowatts(3))
    meterReadingService.storeReadings(MeterReadings(smartMeterId, List(electricityReading, otherReading)))

    val limit = 5
    val expectedPricePlanCost: List[Map[String, Money]] = List(
      Map(pricePlan2Id -> EUR(BigDecimal("14.00"))),
      Map(pricePlan3Id -> EUR(BigDecimal("28.00"))),
      Map(pricePlan1Id -> EUR(BigDecimal("140.00"))),
    )

    Get(s"/price-plans/recommend/$smartMeterId?limit=$limit") ~> controller.routes ~> check {
      responseAs[String] should be (expectedPricePlanCost.asJson.noSpaces)
    }
  }

  "GET /price-plans/recommend/<meterId>" should "returnNotFound" in new Setup {

    Get(s"/price-plans/recommend/$smartMeterId") ~> controller.routes ~> check {
      status should be (StatusCodes.NotFound)
    }
  }

}
