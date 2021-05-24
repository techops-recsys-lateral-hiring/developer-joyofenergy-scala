package com.tw.energy

import com.tw.energy.domain.{ElectricityReading, PricePlan}
import com.tw.energy.generator.Generator
import squants.energy.KilowattHours
import squants.market.EUR

object Configuration {
  private val DR_EVILS_DARK_ENERGY_ENERGY_SUPPLIER = "Dr Evil's Dark Energy"
  private val THE_GREEN_ECO_ENERGY_SUPPLIER = "The Green Eco"
  private val POWER_FOR_EVERYONE_ENERGY_SUPPLIER = "Power for Everyone"
  private val MOST_EVIL_PRICE_PLAN_ID = "price-plan-0"
  private val RENEWABLES_PRICE_PLAN_ID = "price-plan-1"
  private val STANDARD_PRICE_PLAN_ID = "price-plan-2"
  private val SARAHS_SMART_METER_ID = "smart-meter-0"
  private val PETERS_SMART_METER_ID = "smart-meter-1"
  private val CHARLIES_SMART_METER_ID = "smart-meter-2"
  private val ANDREAS_SMART_METER_ID = "smart-meter-3"
  private val ALEXS_SMART_METER_ID = "smart-meter-4"

  val pricePlans: List[PricePlan] = List(
    PricePlan(MOST_EVIL_PRICE_PLAN_ID, DR_EVILS_DARK_ENERGY_ENERGY_SUPPLIER, EUR(10)/KilowattHours(1), List()),
    PricePlan(RENEWABLES_PRICE_PLAN_ID, THE_GREEN_ECO_ENERGY_SUPPLIER, EUR(2)/KilowattHours(1), List()),
    PricePlan(STANDARD_PRICE_PLAN_ID, POWER_FOR_EVERYONE_ENERGY_SUPPLIER, EUR(1)/KilowattHours(1), List())
  )

  val smartMeterToPricePlanAccounts: Map[String, String] = Map(
    SARAHS_SMART_METER_ID -> MOST_EVIL_PRICE_PLAN_ID,
    PETERS_SMART_METER_ID -> RENEWABLES_PRICE_PLAN_ID,
    CHARLIES_SMART_METER_ID -> MOST_EVIL_PRICE_PLAN_ID,
    ANDREAS_SMART_METER_ID -> STANDARD_PRICE_PLAN_ID,
    ALEXS_SMART_METER_ID -> RENEWABLES_PRICE_PLAN_ID
  )

  val smartMeterIds: Set[String] = smartMeterToPricePlanAccounts.keySet

  def generateReadingsMap(): Map[String, List[ElectricityReading]] = {
    smartMeterIds.map(id => (id, Generator.generateReadings(20))).toMap
  }
}
