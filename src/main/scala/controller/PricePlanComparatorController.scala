package controller

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.http.scaladsl.server.Route
import domain.PricePlanCosts
import domain.StringTypes.{PlanName, SmartMeterId}
import io.circe.generic.auto._
import service.{AccountService, PricePlanService}


class PricePlanComparatorController(pricePlanService: PricePlanService, accountService: AccountService) extends JsonSupport {
  def routes: Route = pathPrefix("price-plans") {
    get {
      path("compare-all" / Segment) { smartMeterId =>
        complete(calculatedCostForEachPricePlan(smartMeterId))
      } ~ path("recommend" / Segment) { smartMeterId =>
        parameter('limit.as[Int].?) { limit =>
          complete(recommendCheapestPricePlans(smartMeterId, limit))
        }
      }
    }
  }

  private def calculatedCostForEachPricePlan(smartMeterId: SmartMeterId): ToResponseMarshallable = {
    val maybePricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId)
    val maybeConsumptionsForPricePlans = pricePlanService.consumptionCostByPricePlan(smartMeterId)

    (maybeConsumptionsForPricePlans, maybePricePlanId) match {
      case (Some(consumptions), Some
        (pricePlans)) => PricePlanCosts(pricePlans, consumptions)
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
