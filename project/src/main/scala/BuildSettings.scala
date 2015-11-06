package io.github.rvvincelli.blogpost

import sbt._
import Keys._

object BuildSettings {

  private val buildVersion      = "2.0"
  private val buildScalaVersion = "2.10.5"

  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
    scalacOptions in (Compile, compile) ++= Seq( "-feature", "-language:postfixOps" ),
    javacOptions in (Compile, compile) ++= Seq( "-target", "1.7" )
  )

  val testSettings = Seq(
    testOptions in Test += Tests.Filter { _ endsWith "Spec" }
  )
  
}
