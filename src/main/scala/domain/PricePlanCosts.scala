package domain

case class PricePlanCosts(pricePlanId: Option[String], pricePlanComparisons: Map[String, BigDecimal])
