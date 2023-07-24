package com.tw.energy.service

import com.tw.energy.domain.ElectricityReading
import com.tw.energy.domain.MeterReadings
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant

class MeterReadingServiceTest extends AnyFlatSpec with Matchers {
  val meterId = "meterId"
  val reading: ElectricityReading = ElectricityReading(Instant.now(), 0.8)
  val readings: List[ElectricityReading] = List(reading)

  "getReadings" should "get readings if Id exists" in {

    val service = new MeterReadingService(Map(meterId -> readings))

    service.getReadings(meterId) should be (Some(readings))
  }

  "getReadings" should "get not found result if Id does not exist" in {
    val service = new MeterReadingService()

    service.getReadings("meterId") should be (None)
  }

  "storeReadings" should "create new Entry" in {
    val service = new MeterReadingService()

    service.storeReadings(MeterReadings(meterId, readings))

    service.getReadings(meterId) should be (Some(readings))
  }

  "storeReadings" should "append readings if entry already exists" in {
    val service = new MeterReadingService(Map(meterId -> List(reading)))
    val anotherReading = ElectricityReading(Instant.now(), 0.7)

    service.storeReadings(MeterReadings(meterId, List(anotherReading)))

    service.getReadings(meterId) should be (Some(List(reading, anotherReading)))
  }

}
