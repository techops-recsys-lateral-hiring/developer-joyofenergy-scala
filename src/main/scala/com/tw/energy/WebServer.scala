package com.tw.energy

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object WebServer

class WebServer(val route: Route, val host: String = "localhost", val port: Int = 8080) {
  implicit val system: ActorSystem = ActorSystem("joy-of-energy-system")
  implicit val executionContext: ExecutionContext = system.dispatcher

  def start(): RunningServer = {
    new RunningServer(Http().newServerAt(host, port).bindFlow(route))
  }

  class RunningServer(bindingFuture: Future[Http.ServerBinding]) {
    def stop(): Unit = {
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }

    def stopOnReturn(): Unit = {
      println(s"Server online at http://localhost:$port/\nPress RETURN to stop...")
      StdIn.readLine()
      stop()
    }
  }

}
