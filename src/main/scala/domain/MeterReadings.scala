package domain

import domain.Ids.SmartMeterId

case class MeterReadings(smartMeterId: SmartMeterId, electricityReadings: List[ElectricityReading])
