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

import chisel3._
import chisel3.iotesters.PeekPokeTester

/**
 * A simple tester that just runs some ticks
 */
class KnightTester(dut: KnightRider) extends PeekPokeTester(dut) {

  for (i <- 0 until 30) {
    println(peek(dut.io.led).toString())
    step(1)
  }
}

/**
 * Run the tests at a lower frequency.
 */
object KnightTester extends App {
  iotesters.Driver.execute(Array[String](), () => new KnightRider(null, 12)) {
    c => new KnightTester(c)
  }
}
