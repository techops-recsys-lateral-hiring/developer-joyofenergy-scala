name := "joi-energy-scala"

version := "0.1"

scalaVersion := "2.12.7"

val circeVersion = "0.10.0"
val akkaVersion = "2.5.19"
val akkaHttpVersion = "10.1.7"
val akkaHttpCirceVersion = "1.24.3"
val scalaTestVersion = "3.0.5"

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



