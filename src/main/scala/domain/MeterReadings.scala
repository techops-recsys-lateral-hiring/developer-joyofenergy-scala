package domain

import domain.StringTypes.SmartMeterId

case class MeterReadings(smartMeterId: SmartMeterId, electricityReadings: List[ElectricityReading])
