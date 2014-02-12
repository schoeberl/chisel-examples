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

/**
 * This is a very basic ALU example
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
