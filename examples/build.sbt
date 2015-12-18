
scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src"

// This is the latest release from UCB
// libraryDependencies += "edu.berkeley.cs" %% "chisel" % "latest.release"

// This is from a localally published version
libraryDependencies += "edu.berkeley.cs" %% "chisel" % "2.3-SNAPSHOT"
