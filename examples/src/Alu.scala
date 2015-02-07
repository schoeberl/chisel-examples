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
 *
 * This should be an ALU and not have switches
 * as inputs and leds as output.
 */
class Alu extends Module {
  val io = new Bundle {
    val sw = UInt(INPUT, 10)
    val led = UInt(OUTPUT, 10)
  }

  val fn = io.sw(1, 0)
  val a = io.sw(5, 2)
  val b = io.sw(9, 6)

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
  io.led := result
}

// Generate the Verilog code by invoking chiselMain() in our main()
object AluMain {
  def main(args: Array[String]): Unit = {
    println("Hello")
    chiselMain(args, () => Module(new Alu()))
  }
}

// Test the ALU design
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

        poke(dut.io.sw, (b << 6) + (a << 2) + op)
        step(1) // is a step for truly combinational circuit needed?
        expect(dut.io.led, res.litValue())
      }
    }
  }
}

object AluTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(args, () => Module(new Alu())) {
      f => new AluTester(f)
    }
  }
}
