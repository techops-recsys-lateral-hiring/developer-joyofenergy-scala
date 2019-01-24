package service

import domain.{ElectricityReading, MeterReadings}

class MeterReadingService(private var readings: Map[String, List[ElectricityReading]] = Map()) {
  def getReadings(smartMeterId: String): Option[List[ElectricityReading]] = {
    readings.get(smartMeterId)
  }
}
