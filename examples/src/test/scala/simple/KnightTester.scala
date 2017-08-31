/*
 * Simple FSM example including slow frequency tick generation and
 * reset handling with a top level Chisel component.
 * 
 * This code is part of the Chisel examples.
 * 
 * Copyright: 2015, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * A simple finite state machine (FSM) that drives LEDs like
 * on the lights on the Knight Rider car.
 * 
 */

package simple

import Chisel._

/**
 * A simple tester that just runs some ticks
 */
class KnightTester(dut: KnightRider) extends Tester(dut) {

  for (i <- 0 until 30) {
    println(peek(dut.io.led))
    step(1)
  }
}

/**
 * Run the tests at a lower frequency.
 */
object KnightTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array("--genHarness", "--test", "--backend", "c",
      "--compile", "--targetDir", "generated"),
      () => Module(new KnightRider(null, 12))) {
        c => new KnightTester(c)
      }
  }
}
