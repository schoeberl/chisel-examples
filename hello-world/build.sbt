
scalaVersion := "2.10.2"

addSbtPlugin("com.github.scct" % "sbt-scct" % "0.2.1")

scalaSource in Compile := new File("src")

// libraryDependencies += "edu.berkeley.cs" %% "chisel" % "2.2.19"

libraryDependencies += "edu.berkeley.cs" %% "chisel" % "latest.release"
