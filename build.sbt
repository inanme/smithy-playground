inThisBuild(
  Seq(
    organization := "smithy-playground",
    scalaVersion := Version.scala,
    addCompilerPlugin(Dependencies.kindProjector),
    addCompilerPlugin(Dependencies.betterMonadicFor),
    // https://github.com/oleg-py/better-monadic-for/issues/50
    scalacOptions += "-Wconf:msg=\\$implicit\\$:s",
    semanticdbEnabled := true,                        // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision, // use Scalafix compatible version
    scalafixDependencies += Dependencies.organizeImports,
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= Seq(
      Dependencies.weaver % Test
    )
  ) ++ Def.settings(
    addCommandAlias(
      "validate",
      List(
        "clean",
        "scalafmtCheckAll",
        "scalafmtSbtCheck",
        "scalafixAll --check",
        "compile",
        "test:compile",
        "undeclaredCompileDependenciesTest",
        "unusedCompileDependenciesTest",
        "missinglinkCheck",
        "coverage",
        "test",
        "coverageOff",
        "coverageReport"
      ).mkString(";", "; ", "")
    ),
    addCommandAlias(
      "fmt",
      List(
        "scalafmtAll",
        "scalafmtSbt",
        "scalafixAll"
      ).mkString(";", "; ", "")
    )
  )
)

lazy val `smithy-playground` = project
  .in(file("."))
  .settings(publish / skip := true)
  .aggregate(smithy, http4s)

lazy val smithy = project
  .in(file("modules/smithy"))
  .enablePlugins(Smithy4sCodegenPlugin)
  .disablePlugins(ScalafixPlugin, ScalafmtPlugin)
  .settings(
    name                        := "smithy",
    Compile / smithy4sInputDirs := Seq((Compile / resourceDirectory).value / "META-INF" / "smithy"),
    Compile / smithy4sSmithyLibrary := false,
    Compile / smithy4sModelTransformers += "RemoveTracingHeader",
    libraryDependencies ++= Seq(
      "com.disneystreaming.smithy4s" %% "smithy4s-core" % smithy4sVersion.value,

      // https://github.com/disneystreaming/aws-sdk-smithy-specs
      "com.disneystreaming.smithy4s" %% "smithy4s-aws-kernel" % smithy4sVersion.value,
      "com.disneystreaming.smithy"    % "aws-kinesis-spec"    % Version.smithySpec
    ),
    libraryDependencies := {
      libraryDependencies.value.map { lib =>
        lib.withConfigurations(lib.configurations.map {
          case "smithy4s" => "compile,smithy4s"
          case other      => other
        })
      }
    }
  )
  .dependsOn(transformer)
  .aggregate(transformer)

val transformer = project
  .in(file("modules/smithy-transformer"))
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.smithyModel,
      Dependencies.smithyBuild
    )
  )

lazy val http4s = project
  .in(file("modules/http4s"))
  .dependsOn(smithy)
  .settings(
    name := "http4s-playground",
    libraryDependencies ++= Seq(
      "ch.qos.logback"                % "logback-classic"         % Version.logback,
      "ch.qos.logback"                % "logback-core"            % Version.logback,
      "org.typelevel"                %% "cats-core"               % Version.cats,
      "org.typelevel"                %% "cats-effect"             % Version.catsEffect,
      "org.typelevel"                %% "cats-effect-kernel"      % Version.catsEffect,
      "co.fs2"                       %% "fs2-core"                % Version.fs2,
      "co.fs2"                       %% "fs2-io"                  % Version.fs2,
      "org.http4s"                   %% "http4s-client"           % Version.http4s,
      "org.http4s"                   %% "http4s-ember-client"     % Version.http4s,
      "org.http4s"                   %% "http4s-ember-server"     % Version.http4s,
      "com.disneystreaming.smithy4s" %% "smithy4s-core"           % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-aws-kernel"     % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-aws"            % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-aws-http4s"     % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s"         % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger" % smithy4sVersion.value
    )
  )
