package domain

import java.time.{DayOfWeek, LocalDateTime}

import org.scalatest.{FlatSpec, Matchers}

class PricePlanTest extends FlatSpec with Matchers {
  val unitRate = 3
  val planName = "plan Name"
  val supplierName = "Supplier Name"
  val tuesdayMultiplier = PeakTimeMultiplier(DayOfWeek.TUESDAY, 1.5)
  val wednesdayMultiplier = PeakTimeMultiplier(DayOfWeek.WEDNESDAY, 2.5)
  val pricePlan = PricePlan(supplierName, planName, unitRate, List(tuesdayMultiplier, wednesdayMultiplier))
  val monday = LocalDateTime.parse("2019-01-28T15:59:01")
  val tuesday = LocalDateTime.parse("2019-01-29T15:59:01")
  val wednesday = LocalDateTime.parse("2019-01-30T15:59:01")


  "calculatePrice" should "return standard rate when no peak time multiplier applies" in {
    pricePlan.calculatePrice(monday) should be(3)
  }

  "calculatePrice" should "return modified rate when multiplier applies" in {
    pricePlan.calculatePrice(tuesday) should be(4.5)
    pricePlan.calculatePrice(wednesday) should be(7.5)
  }
}
