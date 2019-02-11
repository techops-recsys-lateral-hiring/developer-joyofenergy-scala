package domain

import java.time.{DayOfWeek, LocalDateTime}

import domain.StringTypes.{EnergySupplier, PlanName}

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
