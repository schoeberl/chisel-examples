/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * An ALU is a minimal start for a processor.
 * 
 */

import Chisel._
import Node._
import scala.collection.mutable.HashMap

/**
 * This is a very basic ALU example.
 */
class Alu extends Module {
  val io = new Bundle {
    val fn = UInt(INPUT, 2)
    val a = UInt(INPUT, 4)
    val b = UInt(INPUT, 4)
    val result = UInt(OUTPUT, 4)
  }

  // Use shorter variable names
  val fn = io.fn
  val a = io.a
  val b = io.b

  val result = UInt(width = 4)
  // some default value is needed
  result := UInt(0)

  // The ALU selection
  switch(fn) {
    is(UInt(0)) { result := a + b }
    is(UInt(1)) { result := a - b }
    is(UInt(2)) { result := a | b }
    is(UInt(3)) { result := a & b }
  }

  // Output on the LEDs (with zero extension)
  io.result := result
}

/**
 * A top level to wire FPGA buttons and LEDs
 * to the ALU input and output.
 */
class AluTop extends Module {
  val io = new Bundle {
    val sw = UInt(INPUT, 10)
    val led = UInt(OUTPUT, 10)
  }

  val alu = Module(new Alu())

  // Map switches to the ALU input ports
  alu.io.fn := io.sw(1, 0)
  alu.io.a := io.sw(5, 2)
  alu.io.b := io.sw(9, 6)

  // And the result to the LEDs (with 0 extension)
  io.led := alu.io.result
}

// Generate the Verilog code by invoking chiselMain() in our main()
object AluMain {
  def main(args: Array[String]): Unit = {
    println("Generating the ALU hardware")
    chiselMain(args, () => Module(new AluTop()))
  }
}

/**
 * Test the Alu design
 */
class AluTester(dut: Alu) extends Tester(dut) {

  // This is exhaustive testing, which usually is not possible
  for (a <- 0 to 15) {
    for (b <- 0 to 15) {
      for (op <- 0 to 3) {
        val result =
          op match {
            case 0 => a + b
            case 1 => a - b
            case 2 => a | b
            case 3 => a & b
          }
        val res = UInt((result & 0x0f), 4)

        poke(dut.io.fn, op)
        poke(dut.io.a, a)
        poke(dut.io.b, b)
        step(1) // for a truly combinational circuit not really needed
        expect(dut.io.result, res.litValue())
      }
    }
  }
}

object AluTester {
  def main(args: Array[String]): Unit = {
    println("Testing the ALU")
    chiselMainTest(args, () => Module(new Alu())) {
      f => new AluTester(f)
    }
  }
}
