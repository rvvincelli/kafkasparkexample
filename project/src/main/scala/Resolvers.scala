package io.github.rvvincelli.blogpost

import sbt._

object Resolvers {

  private val cloudera = "cloudera" at "https://repository.cloudera.com/artifactory/repo/"
  
  private val pentaho = "pentaho" at "http://repository.pentaho.org/artifactory/repo/"

  private val conjars = "conjars" at "http://conjars.org/repo"
  
  val resolvers = Seq(cloudera, pentaho, conjars)
  
}
