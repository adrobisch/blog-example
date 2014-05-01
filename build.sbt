import spray.revolver.RevolverPlugin.Revolver

Revolver.settings.settings

name := "blog"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "io.spray" % "spray-can" % "1.3.1",
  "io.spray" % "spray-routing" % "1.3.1",
  "io.spray" % "spray-httpx" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  "org.json4s" %% "json4s-native" % "3.2.8",
  "org.specs2" %% "specs2" % "2.3.8" % "test",
  "io.spray" % "spray-testkit" % "1.3.1"
)


