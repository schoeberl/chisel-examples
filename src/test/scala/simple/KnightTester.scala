/*
 * Tester for the FSM example Knight Rider.
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
