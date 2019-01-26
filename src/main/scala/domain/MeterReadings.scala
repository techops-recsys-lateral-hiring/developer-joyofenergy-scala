package domain

case class MeterReadings(smartMeterId: String, electricityReadings: List[ElectricityReading])
