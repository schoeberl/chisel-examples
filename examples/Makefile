#
# Building Chisel examples without too much sbt/scala/... stuff
#
# sbt looks for default into a folder ./project and . for build.sdt and Build.scala
# sbt creates per default a ./target folder

SBT = sbt


# Generate Verilog code

alu:
	$(SBT) "run-main simple.AluMain"

knight:
	$(SBT) "run-main simple.KnightMain"

uart:
	$(SBT) "run-main uart.SenderMain"


# Generate the C++ simulation and run the tests

alu-test:
	$(SBT) "run-main simple.AluTester"

counter:
	$(SBT) "run-main simple.CounterTester"

knight-test:
	$(SBT) "run-main simple.KnightTest"

fifo-test:
	$(SBT) "run-main simple.FifoTester"

fifo-view:
	gtkwave generated/BubbleFifo.vcd --save=bubble.gtkw

fifo: fifo-test fifo-view


