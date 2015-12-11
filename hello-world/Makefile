#
# Building a Hello World Chisel without too much sbt/scala/... stuff
#
# sbt looks for default into a folder ./project and . for build.sdt and Build.scala
# sbt creates per default a ./target folder

# Using sbt with a .jar file in the repository. Better install sbt
# SBT = java -Xmx1024M -Xss8M -XX:MaxPermSize=128M -jar sbt/sbt-launch.jar

SBT = sbt

# This generates the Verilog and C++ files by invoking main from
# class HelloMain in package hello.
# The source directory is configured in build.sbt.
# The Scala/Java build directory is default ./target.

# The first two arguments are consumed by sbt, the rest is
# forwarded to the Scala/Chisel main().


# Generate Verilog code
hdl:
	$(SBT) "run-main hello.HelloMain --targetDir generated --backend v"

# Generate C++ code (simulation)
cpp:
	$(SBT) "run-main hello.HelloMain --backend c --compile --targetDir generated"
