package com.tw.energy.domain

import java.time.Instant
import squants.energy.{Kilowatts, Power}

case class ElectricityReading(time: Instant, reading: Power)
