name := "dodown"

organization := "com.github.philcali"

version := "0.1.0"

scalaVersion := "2.9.0"

libraryDependencies ++= Seq (
  "org.jaudiotagger" % "jaudiotagger" % "2.0.1",
  "net.databinder" %% "dispatch-http" % "0.8.5"
)
