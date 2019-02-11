import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import domain.MeterReadings
import generator.Generator
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.Future
import scala.util.Random

class EndpointIntegrationTest extends AsyncFlatSpec with Matchers with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private val application = new JOIEnergyApplication
  private val port = 8000 + Random.nextInt(1000)
  private val baseUrl = s"http://localhost:$port"
  private val server = new WebServer(application.routes, port = port)
  private var runningServer: server.RunningServer = _

  override def beforeAll(): Unit = {
    runningServer = server.start()
  }

  override def afterAll(): Unit = {
    runningServer.stop()
  }

  "readings/store" should "store readings" in {
    val smartMeterId = "bob"
    val readings = MeterReadings(smartMeterId, Generator.generateReadings(20))

    Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"$baseUrl/readings/store", entity = jsonEntity(readings)))
      .map { response =>
        response.status should be(StatusCodes.OK)
      }
  }

  "readings/read" should "return a meter reading associated with meter id" in {
    val smartMeterId = "bob"

    populateMeterReadingsForMeter(smartMeterId).flatMap(_ =>
      Http().singleRequest(HttpRequest(uri = s"$baseUrl/readings/read/$smartMeterId")))
      .map { response =>
        response.status should be(StatusCodes.OK)
      }
  }

  "price-plans/compare-all" should "calculate all prices" in {
    val smartMeterId = "bob"

    populateMeterReadingsForMeter(smartMeterId).flatMap(_ =>
      Http().singleRequest(HttpRequest(uri = s"$baseUrl/price-plans/compare-all/$smartMeterId")))
      .map { response =>
        response.status should be(StatusCodes.OK)
      }
  }

  "price-plans/recommend" should "return recommended cheapest price plans" in {
    val smartMeterId = "bob"

    populateMeterReadingsForMeter(smartMeterId).flatMap(_ =>
      Http().singleRequest(HttpRequest(uri = s"$baseUrl/price-plans/recommend/$smartMeterId?limit=2")))
      .map { response =>
        response.status should be(StatusCodes.OK)
      }
  }

  private def jsonEntity(readings: MeterReadings) = {
    HttpEntity(MediaTypes.`application/json`, readings.asJson.noSpaces)
  }

  private def populateMeterReadingsForMeter(smartMeterId: String): Future[HttpResponse] = {
    val readings = MeterReadings(smartMeterId, Generator.generateReadings(20))

    Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = s"$baseUrl/readings/store", entity = jsonEntity(readings)))
  }
}
