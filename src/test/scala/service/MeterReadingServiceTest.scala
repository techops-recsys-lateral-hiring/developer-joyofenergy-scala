package service

import java.time.Instant

import domain.ElectricityReading
import org.scalatest.{FlatSpec, Matchers}

class MeterReadingServiceTest extends FlatSpec with Matchers {

  "getReadings" should "get readings if Id exists" in {
    val meterId = "meterId"
    val readings = List(ElectricityReading(Instant.now(), 0.8))
    val service = new MeterReadingService(Map(meterId -> readings))

    service.getReadings(meterId) should be (Some(readings))
  }

  "getReadings" should "get not found result if Id does not exist" in {
    val service = new MeterReadingService()

    service.getReadings("meterId") should be (None)
  }

}
