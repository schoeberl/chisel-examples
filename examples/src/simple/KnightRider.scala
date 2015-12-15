/*
 * This code is part of the Chisel examples.
 * 
 * Copyright: 2015, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * A simple finite state machine (FSM) that drives LEDs like
 * on the Knight Rider car.
 * 
 */

package simple

import Chisel._

/**
 * Do some fancy blinking.
 */
class KnightRider(frequ: Int) extends Module {
  val io = new Bundle {
    val led = Bits(OUTPUT, 6)
  }

  val goLeft :: goRight :: Nil = Enum(UInt(), 2)

  val stateReg = Reg(init = goLeft)
  val ledReg = Reg(init = Bits(1, 6))

  val tick = Module(new Tick(frequ))

  // Update FSM state and registers only with low frequency tick
  when(tick.io.tick === Bits(1)) {

    // State change one tick earlier
    when(ledReg(4) === UInt(1)) {
      stateReg := goRight
    }.elsewhen(ledReg(1) === UInt(1)) {
      stateReg := goLeft
    }

    when(stateReg === goLeft) {
      ledReg := ledReg << 1
    }.otherwise {
      ledReg := ledReg >> 1
    }
  }

  io.led := ledReg
}

/**
 * Generate a 6 Hz tick to drive the FSM.
 */
class Tick(frequ: Int) extends Module {
  val io = new Bundle {
    val tick = Bits(OUTPUT, 1)
  }
  val CNT_MAX = UInt(frequ/6 - 1)

  val r1 = Reg(init = UInt(0, 32))

  val limit = r1 === CNT_MAX
  val tick = limit

  r1 := r1 + UInt(1)
  when(limit) {
    r1 := UInt(0)
  }

  io.tick := tick
}

class KnightTester(dut: KnightRider) extends Tester(dut) {

  for (i <- 0 until 30) {
    println(peek(dut.io.led))
    step(1)
  }
}

/**
 * Run the tests at a lower frequency.
 */
object KnightTest {
  def main(args: Array[String]): Unit = {
    chiselMainTest(args, () => Module(new KnightRider(12))) {
      c => new KnightTester(c)
    }
  }
}

object KnightMain {
  def main(args: Array[String]): Unit = {
    // DE2-115 has a 50 MHz clock
    chiselMain(args, () => Module(new KnightRider(50000000)))
  }
}