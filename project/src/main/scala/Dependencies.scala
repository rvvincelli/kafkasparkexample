package io.github.rvvincelli.blogpost

import Versions._
import sbt._

object Dependencies {

  private val argonaut            = "io.argonaut"         %% "argonaut"              % argonautVersion  % "compile" withSources()
  private val kafka               = "org.apache.kafka"    %% "kafka"                 % kafkaVersion     % "compile" withSources() 
  private val hadoopClient        = "org.apache.hadoop"   %  "hadoop-client"         % hadoopVersion    % "compile" withSources() excludeAll ExclusionRule(organization = "javax.servlet")
  private val sparkStreamingKafka = "org.apache.spark"    %% "spark-streaming-kafka" % sparkVersion     % "compile" withSources() 
  private val sparkStreaming      = "org.apache.spark"    %% "spark-streaming"       % sparkVersion     % "compile" withSources()
  private val sparkYarn           = "org.apache.spark"    %% "spark-yarn"            % sparkVersion     % "compile" withSources()
  private val sparkSql            = "org.apache.spark"    %% "spark-sql"             % sparkVersion     % "compile" withSources()
  private val sparkHive           = "org.apache.spark"    %% "spark-hive"            % sparkVersion     % "compile" withSources() exclude("stax", "stax-api")

  private val scalaTest           = "org.scalatest"       %% "scalatest"             % scalaTestVersion  % "test"
  private val scalaCheck          = "org.scalacheck"      %% "scalacheck"            % scalaCheckVersion % "test"
  private val kafkaTest           = "org.apache.kafka"    %% "kafka"                 % kafkaVersion      % "test" withSources() exclude("com.yammer.metrics", "metrics-core") classifier "test"
  private val jUnit               = "junit"               %  "junit"                 % jUnitVersion      % "test"

  val dependencies = Seq(argonaut, kafka, hadoopClient, sparkStreamingKafka, sparkStreaming, sparkYarn, sparkSql, sparkHive, jUnit, scalaTest, scalaCheck, kafkaTest)

}
