package com.tw.energy.domain

import com.tw.energy.domain.StringTypes.EnergySupplier
import com.tw.energy.domain.StringTypes.PlanName

import java.time.DayOfWeek
import java.time.LocalDateTime

case class PricePlan(planName: PlanName, energySupplier: EnergySupplier, unitRate: BigDecimal, peakTimeMultipliers: List[PeakTimeMultiplier] = List()) {
  def calculatePrice(localDateTime: LocalDateTime): BigDecimal = {
    val multiplier: BigDecimal = peakTimeMultipliers
      .find(_.dayOfWeek == localDateTime.getDayOfWeek)
      .map(_.multiplier)
      .getOrElse(1)
    multiplier * unitRate
  }
}

case class PeakTimeMultiplier(dayOfWeek: DayOfWeek, multiplier: BigDecimal)
