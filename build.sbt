ThisBuild / scalaVersion := "2.12.14"
ThisBuild / organization := "com.dandiinc"
ThisBuild / name := "dandi-schemas"

ThisBuild / resolvers ++= Seq(
  "dandi" at "artifactregistry://us-central1-maven.pkg.dev/dandi-engineering/maven"
)

ThisBuild / publishTo := {
  val dandiRepo = "artifactregistry://us-central1-maven.pkg.dev/dandi-engineering/"
  if (isSnapshot.value)
    Some("dandi snapshots" at dandiRepo + "snapshots")
  else
    Some("dandi releases" at dandiRepo + "releases")
}

scalacOptions += "-target:jvm-1.8"
initialize := {
  val _ = initialize.value // Needed to run previous initialization.
  assert(scala.util.Properties.isJavaAtLeast("1.8"), "Ерші project requires Java 8 or later")
}
// Configure Java compiler appropriately.
javacOptions ++= Seq(
  "-source", "1.8",
  "-target", "1.8"
)

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "org.apache.avro" % "avro" % avroCompilerVersion % "provided"
)

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value
)

Compile / packageAvro / publishArtifact := true

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

artifact in(Compile, assembly) := {
  val art = (artifact in(Compile, assembly)).value
  art.withClassifier(Some("assembly"))
}
addArtifact(artifact in(Compile, assembly), assembly)
//crossPaths := false // Do not append Scala versions to the generated artifacts
//autoScalaLibrary := false // This forbids including Scala related libraries into the dependency
//skip in publish := true