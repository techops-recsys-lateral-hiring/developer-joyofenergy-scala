import akka.http.scaladsl.server.Route
import controller.MeterReadingController
import service.MeterReadingService


object JOIEnergyApplication extends App {
  val meterReadingService = new MeterReadingService()
  val meterReadingController = new MeterReadingController(meterReadingService)

  val routes: Route = meterReadingController.routes

  new WebServer(routes).start()
}

