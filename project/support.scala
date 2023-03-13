import sbt._

object Version {
  val betterMonadicFor       = "0.3.1"
  val cats                   = "2.9.0"
  val catsEffect             = "3.4.8"
  val fs2                    = "3.6.1"
  val http4s                 = "0.23.18"
  val kindProjector          = "0.13.2"
  val logback                = "1.4.5"
  val organizeImportsVersion = "0.6.0"
  val scala                  = "2.13.8"
  val smithy                 = "1.28.1"
  val smithySpec             = "2023.02.10"
  val weaver                 = "0.7.15"
}
object Dependencies {
  val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Version.betterMonadicFor
  val kindProjector =
    "org.typelevel" % "kind-projector" % Version.kindProjector cross CrossVersion.full
  val organizeImports =
    "com.github.liancheng" %% "organize-imports" % Version.organizeImportsVersion
  val smithyBuild      = "software.amazon.smithy" % "smithy-build"      % Version.smithy
  val smithyModel      = "software.amazon.smithy" % "smithy-model"      % Version.smithy
  val weaver           = "com.disneystreaming"   %% "weaver-cats"       % Version.weaver
  val weaverScalacheck = "com.disneystreaming"   %% "weaver-scalacheck" % Version.weaver
}
