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
	$(SBT) "testOnly simple.AluTester"

counter-test:
	$(SBT) "testOnly simple.CounterTester"

knight-test:
	$(SBT) "testOnly simple.KnightTester"

fifo-test:
	$(SBT) "testOnly fifo.FifoSpec"

fifo-view:
	gtkwave test_run_dir/BubbleFifo_should_pass/BubbleFifo.vcd --save=bubble.gtkw

fifo: fifo-test fifo-view

# clean everything (including IntelliJ project settings)

clean:
	git clean -fd
