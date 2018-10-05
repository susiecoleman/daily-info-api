name := "daily-info-api"
 
version := "1.0" 
      
lazy val `daily-info-api` = (project in file(".")).enablePlugins(PlayScala)
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  ws,
  "io.monix" %% "monix" % "2.3.3")
