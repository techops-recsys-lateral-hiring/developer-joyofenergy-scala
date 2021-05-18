name := "joi-energy-scala"

version := "0.1"

scalaVersion := "2.13.1"

mainClass in (Compile, run) := Some("com.tw.energy.WebApp")

val circeVersion = "0.12.3"
val akkaVersion = "2.6.1"
val akkaHttpVersion = "10.1.11"
val akkaHttpCirceVersion = "1.30.0"
val scalaTestVersion = "3.1.0"

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
libraryDependencies += "org.typelevel"  %% "squants"  % "1.6.0"
scalacOptions ++= Seq("-deprecation", "-feature")
