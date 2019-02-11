package com.tw.energy.service


import com.tw.energy.domain.StringTypes.{PlanName, SmartMeterId}
import com.tw.energy.domain.{ElectricityReading, PricePlan}

class PricePlanService(pricePlans: Seq[PricePlan], meterReadingService: MeterReadingService) {

  private def calculateAverageReading(readings: Seq[ElectricityReading]): BigDecimal = {
    readings.map(_.reading).sum / readings.length
  }

  private def calculateTimeElapsed(electricityReadings: Seq[ElectricityReading]): BigDecimal = {
    val first = electricityReadings.minBy(_.time)
    val last = electricityReadings.maxBy(_.time)
    java.time.Duration.between(first.time, last.time).getSeconds / 3600.0
  }

  private def calculateCost(readings: Seq[ElectricityReading], plan: PricePlan): BigDecimal = {
    val average = calculateAverageReading(readings)
    val timeElapsed = calculateTimeElapsed(readings)
    val averagedCost = average / timeElapsed
    (averagedCost * plan.unitRate).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  }

  def consumptionCostByPricePlan(smartMeterId: SmartMeterId): Option[Map[PlanName, BigDecimal]] = {
    meterReadingService.getReadings(smartMeterId).map { readings =>
      pricePlans.map(plan => plan.planName -> calculateCost(readings, plan)).toMap
    }
  }
}
