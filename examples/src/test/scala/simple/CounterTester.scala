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
