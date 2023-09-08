inThisBuild(
  List(
    version := "0.1",
    scalaVersion := "3.3.0"
))


val circeVersion = "0.14.5"
val akkaVersion = "2.8.3"
val akkaHttpVersion = "10.5.2"
val akkaHttpCirceVersion = "1.39.2"
val scalaTestVersion = "3.2.16"

lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion // Http Server library: https://doc.akka.io/docs/akka-http/current/server-side/index.html
lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion // Required by akka-http
// lazy val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion

lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test // Test framework: http://www.scalatest.org/
lazy val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test // utilities to test routes: https://doc.akka.io/docs/akka-http/current/routing-dsl/testkit.html
lazy val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test // required by akka-http-testkit

libraryDependencies ++= List(
  akkaHttp,
  akkaStream,
  akkaTestKit,
  akkaHttpTestKit,
  circeCore,
  circeGeneric,
  circeParser,
  scalaTest
)

scalacOptions ++= Seq("-deprecation", "-feature")

//conflictWarning := ConflictWarning.default
conflictManager := ConflictManager.latestRevision