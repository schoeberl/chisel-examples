#
# Building a Hello World Chisel without too much sbt/scala/... stuff
#
# sbt looks for default into a folder ./project and . for build.sdt and Build.scala
# sbt creates per default a ./target folder

SBT = sbt

# This generates the Verilog and C++ files by invoking main from
# class HelloMain in package hello.
# The source directory is configured in build.sbt.
# The Scala/Java build directory is default ./target.

# The first two arguments are consumed by sbt, the rest is
# forwarded to the Scala/Chisel main().



HW_ARGS = --targetDir generated --backend v
TEST_ARGS = --genHarness --test --backend c --compile --targetDir generated

# Generate Verilog code
alu:
	$(SBT) "run-main AluMain $(HW_ARGS)"

uart:
	$(SBT) "run-main UartMain $(HW_ARGS)"

# Generate the C++ simulation and test it
test-alu:
	$(SBT) "run-main AluTester $(TEST_ARGS)"

test-fifo:
	$(SBT) "run-main FifoTester $(TEST_ARGS)"

# Just run a sbt shell
sbt:
	$(SBT)
