package service

import domain.Ids.{AccountId, SmartMeterId}

class AccountService(private val pricePlanIdByAccountId: Map[SmartMeterId, AccountId]) {
  def getPricePlanIdForSmartMeterId(smartMeterId: SmartMeterId): Option[AccountId] = {
    pricePlanIdByAccountId.get(smartMeterId)
  }
}