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
	$(SBT) "runMain uart.SenderMain"


# Generate the C++ simulation and run the tests

alu-test:
	$(SBT) "test:runMain simple.AluTester"

counter:
	$(SBT) "test:runMain simple.CounterTester"

knight-test:
	$(SBT) "test:runMain simple.KnightTester"

fifo-test:
	$(SBT) "test:runMain simple.FifoTester"

fifo-view:
	gtkwave generated/BubbleFifo.vcd --save=bubble.gtkw

fifo: fifo-test fifo-view


