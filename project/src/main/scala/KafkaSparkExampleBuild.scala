package io.github.rvvincelli.blogpost

import sbt._
import Keys._

object TReTECoreBuild extends Build {

  lazy val root = Project(
    id = "kafka-spark-example",
    base = file("."),
    settings =
      BuildSettings.buildSettings ++ 
      Seq(
        libraryDependencies ++= Dependencies.dependencies,
        resolvers ++= Resolvers.resolvers
      )
  )
  
}
