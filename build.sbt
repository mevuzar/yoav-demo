name := "yoav-demo"

version := "1.0"

scalaVersion := "2.11.8"

val akka = "2.4.10"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.akka" %% "akka-actor" % akka,
  "com.typesafe.akka" %% "akka-persistence" % akka,
  "com.typesafe.akka" %% "akka-persistence-query-experimental" % akka,
  "com.typesafe.akka" %% "akka-cluster" % akka,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akka,
  "com.typesafe.akka" %% "akka-remote" % akka,
  "com.typesafe.akka" %% "akka-contrib" % akka,
  "com.typesafe.akka" %% "akka-stream" % "2.4.10",
  "com.typesafe.akka" %% "akka-slf4j" % akka,
  "com.typesafe.akka" %% "akka-http-experimental" % akka,
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akka,
  "org.scalaz" %% "scalaz-core" % "7.2.2",
  "org.atnos" %% "eff-scalaz" % "2.0.0-RC7" exclude("org.scalaz", "scalaz-core"),
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.0" exclude("org.xerial.snappy", "snappy-java"),
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.18",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "com.typesafe.akka" %% "akka-testkit" % akka % "test"
)

autoCompilerPlugins := true

addCompilerPlugin("com.milessabin" % "si2712fix-plugin_2.11.8" % "1.2.0")