package com.tw.energy.controller

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import com.tw.energy.domain.{PricePlanCosts, SquantsJsonSupport}
import com.tw.energy.domain.StringTypes.{PlanName, SmartMeterId}
import com.tw.energy.service.{AccountService, PricePlanService}
import io.circe.generic.auto._


class PricePlanComparatorController(pricePlanService: PricePlanService, accountService: AccountService) extends JsonSupport with SquantsJsonSupport {
  def routes: Route = pathPrefix("price-plans") {
    get {
      path("compare-all" / Segment) { smartMeterId =>
        complete(calculatedCostForEachPricePlan(smartMeterId))
      } ~ path("recommend" / Segment) { smartMeterId =>
        parameter(Symbol("limit").as[Int].?) { limit =>
          complete(recommendCheapestPricePlans(smartMeterId, limit))
        }
      }
    }
  }

  private def calculatedCostForEachPricePlan(smartMeterId: SmartMeterId): ToResponseMarshallable = {
    val maybePricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId)
    val maybeConsumptionsForPricePlans = pricePlanService.consumptionCostByPricePlan(smartMeterId)

    maybeConsumptionsForPricePlans match {
      case Some(consumptions) => PricePlanCosts(maybePricePlanId, consumptions)
      case _ => StatusCodes.NotFound
    }
  }

  private def recommendCheapestPricePlans(smartMeterId: PlanName, limit: Option[Int]): ToResponseMarshallable = {
    val maybeConsumptionsForPricePlans = pricePlanService.consumptionCostByPricePlan(smartMeterId)

    maybeConsumptionsForPricePlans match {
      case Some(consumptions) => consumptions.toSeq
          .sortBy(_._2)
          .map(consumptionEntry => Map(consumptionEntry))
          .take(limit.getOrElse(consumptions.size))
      case _ => StatusCodes.NotFound
    }
  }


}
