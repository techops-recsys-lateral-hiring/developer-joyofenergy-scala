package com.tw.energy.service

import com.tw.energy.domain.StringTypes.AccountId
import com.tw.energy.domain.StringTypes.SmartMeterId

class AccountService(private val pricePlanIdByAccountId: Map[SmartMeterId, AccountId]) {
  def getPricePlanIdForSmartMeterId(smartMeterId: SmartMeterId): Option[AccountId] = {
    pricePlanIdByAccountId.get(smartMeterId)
  }
}