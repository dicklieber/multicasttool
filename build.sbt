import com.typesafe.sbt.packager.SettingsHelper.makeDeploymentSettings

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"
enablePlugins(JavaAppPackaging, GitPlugin, BuildInfoPlugin, SbtTwirl, UniversalPlugin)
buildInfoKeys ++= Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, maintainer,
  git.gitCurrentTags, git.gitCurrentBranch, git.gitHeadCommit, git.gitHeadCommitDate, git.baseVersion)
buildInfoPackage := "org.wa9nnn.fdcluster"

buildInfoOptions ++= Seq(
  BuildInfoOption.ToJson,
  BuildInfoOption.BuildTime,
//  BuildInfoOption.Traits("org.wa9nnn.multicasttool.BuildInfoBase")
)


lazy val root = (project in file("."))
  .settings(
    name := "MulticastTool"
  )

makeDeploymentSettings(Universal, packageBin in Universal, "zip")

val logbackVersion = "1.2.3"

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "18.0.1-R27",
  "io.dropwizard.metrics" % "metrics-core" % "4.2.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "ch.qos.logback" % "logback-core" % logbackVersion,
  "commons-io" % "commons-io" % "2.11.0",
  "com.typesafe.play" %% "play-json" % "2.9.2",

)