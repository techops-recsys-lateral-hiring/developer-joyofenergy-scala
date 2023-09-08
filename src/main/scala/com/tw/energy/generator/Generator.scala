package com.tw.energy.generator

import com.tw.energy.domain.ElectricityReading

import java.time.Instant
import scala.util.Random

object Generator {
  def generateReadings(
                        number: Int,
                        deltaSeconds: Int = 10,
                        time: Instant = Instant.now(),
                        random: Random = new Random(new java.util.Random())
                      ): List[ElectricityReading] = {
    (0 until number)
      .map(i => ElectricityReading(time.minusSeconds(deltaSeconds * i), random.nextGaussian().abs))
      .sortBy(_.time)
      .toList
  }

}
