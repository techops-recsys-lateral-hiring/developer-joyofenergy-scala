package com.tw.energy.domain

import java.time.{DayOfWeek, LocalDateTime}

import org.scalatest.{FlatSpec, Matchers}

class PricePlanTest extends FlatSpec with Matchers {
  private val unitRate = 3
  private val planName = "plan Name"
  private val supplierName = "Supplier Name"
  private val tuesdayMultiplier = PeakTimeMultiplier(DayOfWeek.TUESDAY, 1.5)
  private val wednesdayMultiplier = PeakTimeMultiplier(DayOfWeek.WEDNESDAY, 2.5)
  private val pricePlan = PricePlan(planName, supplierName, unitRate, List(tuesdayMultiplier, wednesdayMultiplier))
  private val monday = LocalDateTime.parse("2019-01-28T15:59:01")
  private val tuesday = LocalDateTime.parse("2019-01-29T15:59:01")
  private val wednesday = LocalDateTime.parse("2019-01-30T15:59:01")


  "calculatePrice" should "return standard rate when no peak time multiplier applies" in {
    pricePlan.calculatePrice(monday) should be(3)
  }

  "calculatePrice" should "return modified rate when multiplier applies" in {
    pricePlan.calculatePrice(tuesday) should be(4.5)
    pricePlan.calculatePrice(wednesday) should be(7.5)
  }
}
