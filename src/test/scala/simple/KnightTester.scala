/*
 * Tester for the FSM example Knight Rider.
 */

package simple

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

/**
 * A simple tester that just runs some ticks
 */
class KnightTester extends AnyFlatSpec with ChiselScalatestTester {

  "CounterTester test" should "pass" in {
    test(new KnightRider(null, 12)) { dut =>
      for (i <- 0 until 30) {
        println(dut.io.led.peek.toString())
        dut.clock.step(1)
      }
    }
  }
}
