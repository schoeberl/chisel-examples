# Chisel Examples

This repository is a collection of code examples for [Chisel](https://chisel.eecs.berkeley.edu/).

This collection has been moved to the latest version of Chisel, Chisel 3.
I have collected notes on this move in [TowardsChisel3](TowardsChisel3.md)

# Getting the Examples

    $ git clone https://github.com/schoeberl/chisel-examples.git

The collection is organized as follows:

**hello-world** is a self contained minimal project for a blinking LED in an FPGA.

The rest of the examples are rooted in the current folder.

# Needed Tools

 * A recent version of Java (JDK 8 or later)

 * The Scala build tool [sbt](http://www.scala-sbt.org/)


# Running the examples

make alu
	Generates the Verilog files for the small ALU.
	Synthesize it for the DE0 board with Quartus and the alu project file.

make alu-test
	Generats the C++ based simulation and runs the tests.

See the Makefile for further examples, or simply run `sbt run` to see all objects with a main.

## Notes using the DE10-Nano

Change switches for FPGA configuration to:

```
+------+
|* ** *|
| *  * |
+------+
```

Probably add USB blaster permissions for: Bus 001 Device 005: ID 09fb:6810 Altera and 09fb:6010

A TTL UART is connected to GPIO pins 1 and 2 of GPIO 0.

```
GND * *
    * *
    * *
    * *
    * *
txd * * rxd (pin 1)
```

rxd and txd are from the FPGA view, therefore TTL UART rxd needs to
be connected to txd (pin 2) and the other way around.
