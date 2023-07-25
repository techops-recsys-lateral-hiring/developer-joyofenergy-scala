inThisBuild(
  List(
    version := "0.1",
    scalaVersion := "2.13.11"
))

val akkaVersion = "2.8.3"
val akkaHttpVersion = "10.5.2"
val scalaTestVersion = "3.2.16"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpVersion // Http Server library: https://doc.akka.io/docs/akka-http/current/server-side/index.html
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion // Required by akka-http

// use akka's spray json support rather than circe as Heiko Seeberger's adapters not released for Scala 3
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion

libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % Test //Test framework: http://www.scalatest.org/
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test //utilities to test routes: https://doc.akka.io/docs/akka-http/current/routing-dsl/testkit.html
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test //required by akka-http-testkit

scalacOptions ++= Seq("-deprecation", "-feature", "-Xsource:3", "-Ywarn-unused")
