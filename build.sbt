import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.mikeycaine",
      scalaVersion := "2.12.2",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "tweets2kafka",
    libraryDependencies += scalaTest % Test,
	libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.16",
	libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0",
	libraryDependencies += "org.json4s" % "json4s-native_2.12" % "3.5.2"
  )
