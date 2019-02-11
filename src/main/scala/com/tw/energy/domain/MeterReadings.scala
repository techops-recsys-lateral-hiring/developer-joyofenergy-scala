package com.tw.energy.domain

import com.tw.energy.domain.StringTypes.SmartMeterId

case class MeterReadings(smartMeterId: SmartMeterId, electricityReadings: List[ElectricityReading])
