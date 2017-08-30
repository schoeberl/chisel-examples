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
 * Do some fancy blinking.
 */
class KnightRider(resetSignal: Bool = null, frequ: Int)
    extends Module(_reset = resetSignal) {
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
  val CNT_MAX = UInt(frequ / 6 - 1)

  val r1 = Reg(init = UInt(0, 32))

  val limit = r1 === CNT_MAX
  val tick = limit

  r1 := r1 + UInt(1)
  when(limit) {
    r1 := UInt(0)
  }

  io.tick := tick
}

/**
 * This is a reset "generator". However, this works ONLY
 * in an FPGA when registers are reset to 0 on FPGA configuration.
 *
 * However, as we do not have a chance to specify register power up
 * attributes, this is a fragile solution (did not work for 2 or 3
 * bit wide counters).
 */
class ResetGen(resetSignal: Bool = null) extends Module {
  val io = new Bundle {
    val resetOut = Bool(OUTPUT)
  }

  val cnt = Reg(UInt(width = 4))

  when(cnt =/= UInt(15)) {
    cnt := cnt + UInt(1)
    io.resetOut := Bool(true)
  }.otherwise {
    cnt := cnt // this should not be needed, but without it the gen. code is wrong
    io.resetOut := Bool(false)
  }
}


/**
 * Top level to connect the button to the reset.
 */
class KnightTop extends Module {
  val io = new Bundle {
    val btn = UInt(INPUT, 4)
    val led = Bits(OUTPUT, 6)
  }

  // Invert the reset button and two flip-flop input synchronization
  val manReset = (~io.btn(3)).toBool
  val syncBtn = Reg(next = Reg(next = manReset))

  val resGen = Module(new ResetGen(Bool(false)))

  // Manual or generated reset
  val resetVal = syncBtn || resGen.io.resetOut

  // DE2-115 has a 50 MHz clock
  val knight = Module(new KnightRider(resetVal, 50000000))

  io.led <> knight.io.led
}

object KnightMain {
  def main(args: Array[String]): Unit = {
    chiselMain(Array("--backend", "v", "--targetDir", "generated"),
      () => Module(new KnightTop()))
  }
}