import sbt.*

object Version {
  val betterMonadicFor       = "0.3.1"
  val cats                   = "2.10.0"
  val catsEffect             = "3.5.1"
  val fs2                    = "3.9.2"
  val http4s                 = "0.23.23"
  val kindProjector          = "0.13.2"
  val logback                = "1.4.8"
  val organizeImportsVersion = "0.6.0"
  val scala                  = "2.13.11"
  val smithy                 = "1.38.0"
  val smithySpec             = "2023.02.10"
  val weaver                 = "0.8.3"
}
object Dependencies {
  val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Version.betterMonadicFor
  val kindProjector =
    "org.typelevel" % "kind-projector" % Version.kindProjector cross CrossVersion.full
  val smithyBuild      = "software.amazon.smithy" % "smithy-build"      % Version.smithy
  val smithyModel      = "software.amazon.smithy" % "smithy-model"      % Version.smithy
  val weaver           = "com.disneystreaming"   %% "weaver-cats"       % Version.weaver
  val weaverScalacheck = "com.disneystreaming"   %% "weaver-scalacheck" % Version.weaver
}
