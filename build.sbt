val scala3Version = "3.5.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Saturn",
    version := "1.0.0-SNAPSHOT",

    scalaVersion := scala3Version,
    assembly / assemblyJarName := "Saturn.jar",

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "com.lihaoyi" %% "upickle" % "3.1.0"
  )
