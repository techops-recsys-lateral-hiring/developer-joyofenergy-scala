package service

import domain.{ElectricityReading, MeterReadings}

class MeterReadingService(private[service] var readingsByMeterId: Map[String, Seq[ElectricityReading]] = Map()) {
  def getReadings(smartMeterId: String): Option[Seq[ElectricityReading]] = {
    readingsByMeterId.get(smartMeterId)
  }

  def storeReadings(meterReadings: MeterReadings): Unit = {
    val existingReadings = readingsByMeterId.getOrElse(meterReadings.smartMeterId, Seq())
    val updatedReadings = existingReadings ++ meterReadings.electricityReadings
    readingsByMeterId += (meterReadings.smartMeterId -> updatedReadings)
  }
}
