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

# Maybe change at some point to have the main class as a define
# that can be overwriteen, such as:
#MAIN=example.AluMain

HW_ARGS = --targetDir generated --backend v
TEST_ARGS = --genHarness --test --backend c --compile --targetDir generated
TEST_ARGS_VCD = --genHarness --test --backend c --compile --vcd --targetDir generated

# Generate Verilog code
alu:
	$(SBT) "run-main AluMain $(HW_ARGS)"

knight:
	$(SBT) "run-main simple.KnightMain $(HW_ARGS)"

uart:
	$(SBT) "run-main UartMain $(HW_ARGS)"

# Generate the C++ simulation and test it
alu-test:
	$(SBT) "run-main AluTester $(TEST_ARGS)"

# TODO: decide on a naming convention related to testing

knight-test:
	$(SBT) "run-main simple.KnightTest $(TEST_ARGS)"

fifo-test:
	$(SBT) "run-main simple.FifoTester $(TEST_ARGS_VCD)"

fifo-view:
	gtkwave generated/BubbleFifo.vcd --save=bubble.gtkw

fifo: fifo-test fifo-view



counter:
	$(SBT) "run-main simple.CounterMain $(TEST_ARGS)"
