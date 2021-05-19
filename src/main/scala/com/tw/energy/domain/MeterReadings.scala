package com.tw.energy.domain

import com.tw.energy.domain.StringTypes.SmartMeterId
import io.circe.generic.JsonCodec

case class MeterReadings(smartMeterId: SmartMeterId, electricityReadings: List[ElectricityReading])
