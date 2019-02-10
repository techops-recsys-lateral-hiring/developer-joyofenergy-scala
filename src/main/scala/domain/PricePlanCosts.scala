package domain

case class PricePlanCosts(pricePlanId: String, pricePlanComparisons: Map[String, BigDecimal])
