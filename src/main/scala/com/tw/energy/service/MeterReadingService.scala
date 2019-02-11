package com.tw.energy.service

import com.tw.energy.domain.StringTypes.SmartMeterId
import com.tw.energy.domain.{ElectricityReading, MeterReadings}

class MeterReadingService(private[service] var readingsByMeterId: Map[SmartMeterId, Seq[ElectricityReading]] = Map()) {
  def getReadings(smartMeterId: SmartMeterId): Option[Seq[ElectricityReading]] = {
    readingsByMeterId.get(smartMeterId)
  }

  def storeReadings(meterReadings: MeterReadings): Unit = {
    val existingReadings = readingsByMeterId.getOrElse(meterReadings.smartMeterId, Seq())
    val updatedReadings = existingReadings ++ meterReadings.electricityReadings
    readingsByMeterId += (meterReadings.smartMeterId -> updatedReadings)
  }
}
