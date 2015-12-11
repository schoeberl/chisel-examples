
scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src"

libraryDependencies += "edu.berkeley.cs" %% "chisel" % "latest.release"
