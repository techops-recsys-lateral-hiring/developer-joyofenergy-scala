package controller

import java.time.Instant

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import domain.{ElectricityReading, MeterReadings, PricePlan, PricePlanCosts}
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest.{FlatSpec, Matchers}
import service.{AccountService, MeterReadingService, PricePlanService}

class PricePlanComparatorControllerTest extends FlatSpec with Matchers with ScalatestRouteTest {
  val pricePlan1Id = "test-supplier"
  val pricePlan2Id = "best-supplier"
  val pricePlan3Id = "second-best-supplier"
  val smartMeterId = "smart-meter-id"

  val pricePlan1 = PricePlan(null, pricePlan1Id, 10, null)
  val pricePlan2 = PricePlan(null, pricePlan2Id, 1, null)
  val pricePlan3 = PricePlan(null, pricePlan3Id, 2, null)

  trait Setup {
    val meterReadingService = new MeterReadingService()
    val pricePlanService = new PricePlanService(List(pricePlan1, pricePlan2, pricePlan3), meterReadingService)
    val accountService = new AccountService(Map(smartMeterId -> pricePlan1Id))
    val controller = new PricePlanComparatorController(pricePlanService, accountService)
  }


  "GET /price-plans/compare-all/<meterId>" should "compare price plans" in new Setup {
    val electricityReading = ElectricityReading(Instant.now().minusSeconds(3600), 15)
    val otherReading = ElectricityReading(Instant.now(), 5)
    meterReadingService.storeReadings(MeterReadings(smartMeterId, List(electricityReading, otherReading)))

    val expectedPricePlanCost: Map[String, BigDecimal] = Map(
      pricePlan1Id -> BigDecimal("100.00"),
      pricePlan2Id -> BigDecimal("10.00"),
      pricePlan3Id -> BigDecimal("20.00"),
    )

    val expected = PricePlanCosts(pricePlan1Id, expectedPricePlanCost)

    Get(s"/price-plans/compare-all/$smartMeterId") ~> controller.routes ~> check {
      responseAs[String] should be (expected.asJson.noSpaces)
    }
  }

  "GET /price-plans/recommend/<meterId>" should "recommend cheapest price plan" in new Setup {
    val electricityReading = ElectricityReading(Instant.now().minusSeconds(1800), 35)
    val otherReading = ElectricityReading(Instant.now(), 3)
    meterReadingService.storeReadings(MeterReadings(smartMeterId, List(electricityReading, otherReading)))

    val expectedPricePlanCost: List[Map[String, BigDecimal]] = List(
      Map(pricePlan2Id -> BigDecimal("38.00")),
      Map(pricePlan3Id -> BigDecimal("76.00")),
      Map(pricePlan1Id -> BigDecimal("380.00")),
    )

    Get(s"/price-plans/recommend/$smartMeterId") ~> controller.routes ~> check {
      responseAs[String] should be (expectedPricePlanCost.asJson.noSpaces)
    }
  }

  "GET /price-plans/recommend/<meterId>" should "recommend limited cheapest price plan" in new Setup {
    val electricityReading = ElectricityReading(Instant.now().minusSeconds(2700), 5)
    val otherReading = ElectricityReading(Instant.now(), 20)
    meterReadingService.storeReadings(MeterReadings(smartMeterId, List(electricityReading, otherReading)))

    val limit = 2
    val expectedPricePlanCost: List[Map[String, BigDecimal]] = List(
      Map(pricePlan2Id -> BigDecimal("16.67")),
      Map(pricePlan3Id -> BigDecimal("33.33"))
    )

    Get(s"/price-plans/recommend/$smartMeterId?limit=$limit") ~> controller.routes ~> check {
      responseAs[String] should be (expectedPricePlanCost.asJson.noSpaces)
    }
  }

  "GET /price-plans/recommend/<meterId>" should "recommend all price plans if limit is high" in new Setup {
    val electricityReading = ElectricityReading(Instant.now().minusSeconds(3600), 25)
    val otherReading = ElectricityReading(Instant.now(), 3)
    meterReadingService.storeReadings(MeterReadings(smartMeterId, List(electricityReading, otherReading)))

    val limit = 5
    val expectedPricePlanCost: List[Map[String, BigDecimal]] = List(
      Map(pricePlan2Id -> BigDecimal("14.00")),
      Map(pricePlan3Id -> BigDecimal("28.00")),
      Map(pricePlan1Id -> BigDecimal("140.00")),
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
