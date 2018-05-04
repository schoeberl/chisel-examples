# Chisel Examples

This repository is a collection of code examples for [Chisel](https://chisel.eecs.berkeley.edu/).

This collection will be moved to the latest version of Chisel, Chisel 3.
I will collect notes in [TowardsChisel3](TowardsChisel3.md)

The blinking LED (in hello-world) has been ported to Chisel 3.
The other examples are still in Chisel 2.

# Getting the Examples

    $ git clone https://github.com/schoeberl/chisel-examples.git

The collection is organized as follows:

**hello-world** is a self contained minimal project for a blinking LED in an FPGA.

The rest of the examples are rooted in the current folder.

# Needed Tools

 * A recent version of Java

 * The Scala build tool [sbt](http://www.scala-sbt.org/)


# Running the examples

make alu
	Generates the Verilog files for the small ALU.
	Synthesize it for the DE0 board with Quartus and the alu project file.

make alu-test
	Generats the C++ based simulation and runs the tests.

See the Makefile for further examples, or simply run `sbt run` to see all objects with a main.
