package service

import domain.StringTypes.{PlanName, SmartMeterId}
import domain.{ElectricityReading, PricePlan}

import scala.concurrent.duration.{Duration, FiniteDuration}

class PricePlanService(pricePlans: Seq[PricePlan], meterReadingService: MeterReadingService) {

  private def calculateAverageReading(readings: Seq[ElectricityReading]): BigDecimal = {
    readings.map(_.reading).sum / readings.length
  }

  private def calculateTimeElapsed(electricityReadings: Seq[ElectricityReading]): FiniteDuration = {
    val first = electricityReadings.minBy(_.time)
    val last = electricityReadings.maxBy(_.time)
    Duration.fromNanos(java.time.Duration.between(first.time, last.time).toNanos)
  }

  private def calculateCost(readings: Seq[ElectricityReading], plan: PricePlan): BigDecimal = {
    val average = calculateAverageReading(readings)
    val timeElapsed = calculateTimeElapsed(readings)
    average / timeElapsed.toHours
  }

  def consumptionCostByPricePlan(smartMeterId: SmartMeterId): Option[Map[PlanName, BigDecimal]] = {
    meterReadingService.getReadings(smartMeterId).map { readings =>
      pricePlans.map(plan => plan.planName -> calculateCost(readings, plan)).toMap
    }
  }
}
