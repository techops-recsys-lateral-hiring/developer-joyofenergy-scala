inThisBuild(
  List(
    version := "0.1",
    scalaVersion := "2.13.11")
)

val circeVersion = "0.14.5"
val akkaVersion = "2.8.2"
val akkaHttpVersion = "10.5.2"
val akkaHttpCirceVersion = "1.39.2"
val scalaTestVersion = "3.2.16"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpVersion // Http Server library: https://doc.akka.io/docs/akka-http/current/server-side/index.html
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion // Required by akka-http

//json library: https://circe.github.io/circe/
libraryDependencies += "io.circe" %% "circe-core" % circeVersion
libraryDependencies += "io.circe" %% "circe-generic" % circeVersion
libraryDependencies += "io.circe" %% "circe-parser" % circeVersion
libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion

libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % Test //Test framework: http://www.scalatest.org/
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test //utilities to test routes: https://doc.akka.io/docs/akka-http/current/routing-dsl/testkit.html
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test //required by akka-http-testkit

scalacOptions ++= Seq("-deprecation", "-feature", "-Xsource:3")
