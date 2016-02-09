/*
 * Copyright: 2015, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Testing register width inference according to original tutorial code.
 * 
 */

package questions

import Chisel._

/**
 * A simple, configurable counter that wraps around.
 */
class RegSize(size: Int) extends Module {
  val io = new Bundle {
    val out = UInt(OUTPUT, size)
  }

  val r1 = Reg(init = UInt(0, size))
  r1 := r1 + UInt(1)

  io.out := r1
}

/**
 * Test the counter by printing out the value at each clock cycle.
 */
class RegSizeTester(c: RegSize) extends Tester(c) {

  for (i <- 0 until 5) {
    println(i)
    println(peek(c.io.out))
    step(1)
  }
}

/**
 * Create a counter and a tester.
 */
object RegSizeTester {
  def main(args: Array[String]): Unit = {
    println("Test register size")
    chiselMainTest(args, () => Module(new RegSize(4))) {
      c => new RegSizeTester(c)
    }
  }
}
