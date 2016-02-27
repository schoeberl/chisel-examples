/*
 * Copyright: 2015, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * A simple counter example with configurable bit width and with a test bench.
 * 
 */

package simple

import Chisel._

/**
 * A simple, configurable counter that wraps around.
 */
class Counter(size: Int) extends Module {
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
class CounterTester(c: Counter) extends Tester(c) {

  for (i <- 0 until 5) {
    println(i)
    println(peek(c.io.out))
    step(1)
  }
}

/**
 * Create a counter and a tester.
 */
object CounterTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array("--genHarness", "--test", "--backend", "c",
      "--compile", "--targetDir", "generated"),
      () => Module(new Counter(4))) {
        c => new CounterTester(c)
      }
  }
}
