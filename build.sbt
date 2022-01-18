name := "meteo-france-client"

version := "0.1"

scalaVersion := "3.0.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % Test
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.10"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "4.9.3"

// libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.1"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-scala
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.1"

// libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.13.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.0" % "provided"

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.11.0"
