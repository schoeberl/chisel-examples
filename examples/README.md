
This is a collection of small Chisel example circuits.

Author: Martin Schoeberl (martin@jopdesign.com)

HOWTO:

make alu
	Generates the Verilog files for the small ALU.
	Synthesize it for the DE0 board with Quartus and the alu project file.

make test-alu
	Generats the C++ based simulation and runs the tests.

## Projects

### altera

# JTAG communication

Explores the JTAG based communication with Altera's alt_jtag_atlantic component.

Not much information is publicly available, see:

 * [Tommy's version in Verilog as part of yariv](https://github.com/tommythorn/yarvi)
 * [Usage with Bluespec Verilog](https://github.com/thotypous/alterajtaguart)

For a first experiment use the nios2-terminal with the design to see an echo of
a character typed inceremented by one (type 'a' and the echo is 'b').
