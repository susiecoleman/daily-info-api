name := "daily-info-api"
 
version := "1.0" 
      
lazy val `daily-info-api` = (project in file(".")).enablePlugins(PlayScala)
      
scalaVersion := "2.12.2"


val http4sVersion = "0.18.20"
libraryDependencies ++= Seq(
  ws,
  "io.monix" %% "monix" % "2.3.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion


)
