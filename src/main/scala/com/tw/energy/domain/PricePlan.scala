package com.tw.energy.domain

import java.time.{DayOfWeek, LocalDateTime}
import com.tw.energy.domain.StringTypes.{EnergySupplier, PlanName}
import squants.{Energy, Price}

case class PricePlan(planName: PlanName, energySupplier: EnergySupplier, unitRate: Price[Energy], peakTimeMultipliers: List[PeakTimeMultiplier] = List()) {
  def calculatePrice(localDateTime: LocalDateTime): Price[Energy] = {
    val multiplier: BigDecimal = peakTimeMultipliers
      .find(_.dayOfWeek == localDateTime.getDayOfWeek)
      .map(_.multiplier)
      .getOrElse(1)
    multiplier * unitRate
  }
}

case class PeakTimeMultiplier(dayOfWeek: DayOfWeek, multiplier: BigDecimal)
