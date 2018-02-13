#
# Building Chisel examples without too much sbt/scala/... stuff
#
# sbt looks for default into a folder ./project and . for build.sdt and Build.scala
# sbt creates per default a ./target folder

SBT = sbt


# Generate Verilog code

alu:
	$(SBT) "runMain simple.AluMain"

knight:
	$(SBT) "runMain simple.KnightMain"

uart:
	$(SBT) "runMain uart.UartMain"


# Generate the C++ simulation and run the tests

alu-test:
	$(SBT) "runMain simple.AluTester"

counter:
	$(SBT) "runMain simple.CounterTester"

knight-test:
	$(SBT) "runMain simple.KnightTest"

fifo-test:
	$(SBT) "runMain simple.FifoTester"

fifo-view:
	gtkwave generated/BubbleFifo.vcd --save=bubble.gtkw

fifo: fifo-test fifo-view


