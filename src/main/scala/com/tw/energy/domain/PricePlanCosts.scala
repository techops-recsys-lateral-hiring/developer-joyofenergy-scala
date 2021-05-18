package com.tw.energy.domain

import squants.market.Money

case class PricePlanCosts(pricePlanId: Option[String], pricePlanComparisons: Map[String, Money])
