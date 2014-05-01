import spray.revolver.RevolverPlugin.Revolver

Revolver.settings.settings

name := "blog"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  "io.spray" %% "spray-can" % "1.3.1-20140423",
  "io.spray" %% "spray-routing" % "1.3.1-20140423",
  "io.spray" %% "spray-httpx" % "1.3.1-20140423",
  "org.json4s" %% "json4s-native" % "3.2.9",
  "org.specs2" %% "specs2" % "2.3.11" % "test",
  "io.spray" %% "spray-testkit" % "1.3.1-20140423"
)

resolvers += "spray repository" at "http://repo.spray.io/"
