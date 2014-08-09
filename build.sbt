import spray.revolver.RevolverPlugin.Revolver
import scoverage.ScoverageSbtPlugin._

Revolver.settings.settings

instrumentSettings

ScoverageKeys.highlighting := true

name := "blog"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "io.spray" %% "spray-can" % "1.3.1",
  "io.spray" %% "spray-routing" % "1.3.1",
  "io.spray" %% "spray-httpx" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "de.sven-jacobs" % "loremipsum" % "1.0",
  "org.specs2" %% "specs2" % "2.4" % "test",
  "io.spray" %% "spray-testkit" % "1.3.1" % "test"
)

addCommandAlias("test", "scoverage:test")
