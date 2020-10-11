import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.mikeycaine",
      scalaVersion := "2.13.3",
      sbtVersion   := "1.3.13",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "tweets2kafka",
    libraryDependencies += scalaTest % Test,
	  libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.5",
	  libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.1.2",
    libraryDependencies += "com.jayway.jsonpath" % "json-path" % "2.3.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.28"

    /*,
	libraryDependencies += "org.json4s" % "json4s-native_2.12" % "3.5.2",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1",
    libraryDependencies += "com.jayway.jsonpath" % "json-path" % "0.5.5"*/
  )
