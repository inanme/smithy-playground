val Versions = new {
  val sbtMissinglink     = "0.3.5"
  val missinglinkCore    = "0.2.7"
  val explicitdeps       = "0.3.1"
  val scalafix           = "0.11.0"
  val weaver             = "0.8.3"
  val tpolecat           = "0.4.2"
  val organizeImports    = "0.6.0"
  val scalafmt           = "2.5.0"
  val smithy4sSbtCodegen = "0.17.10"
}

addSbtPlugin("ch.epfl.scala"                % "sbt-missinglink"      % Versions.sbtMissinglink)
addSbtPlugin("ch.epfl.scala"                % "sbt-scalafix"         % Versions.scalafix)
addSbtPlugin("com.disneystreaming.smithy4s" % "smithy4s-sbt-codegen" % Versions.smithy4sSbtCodegen)
addSbtPlugin("com.github.cb372"          % "sbt-explicit-dependencies" % Versions.explicitdeps)
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"              % Versions.tpolecat)
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"              % Versions.scalafmt)
