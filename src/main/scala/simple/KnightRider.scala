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
import chisel3.util._

/**
 * Do some fancy blinking.
  *
  * TODO: find out the new way to have a reset signal
 */
class KnightRider(resetSignal: Bool = null, frequ: Int)
    extends Module(_reset = resetSignal) {
    // extends Module {
  val io = IO(new Bundle {
    val led = Output(Bits(6.W))
  })

  val goLeft :: goRight :: Nil = Enum(2)

  val stateReg = RegInit(goLeft)
  val ledReg = RegInit(1.U(6.W))

  val tick = Module(new Tick(frequ))

  // Update FSM state and registers only with low frequency tick
  when(tick.io.tick === 1.U) {

    // State change one tick earlier
    when(ledReg(4) === 1.U) {
      stateReg := goRight
    }.elsewhen(ledReg(1) === 1.U) {
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
  val io = IO(new Bundle {
    val tick = Output(Bits(1.W))
  })
  val CNT_MAX = (frequ / 6 - 1).asUInt()

  val r1 = RegInit(0.U(32.W))

  val limit = r1 === CNT_MAX
  val tick = limit

  r1 := r1 + 1.U
  when(limit) {
    r1 := 0.U
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
class ResetGen() extends Module {
  val io = IO(new Bundle {
    val resetOut = Output(Bool())
  })

  val cnt = Reg(UInt(4.W))

  when(cnt =/= 15.U) {
    cnt := cnt + 1.U
    io.resetOut := true.B
  }.otherwise {
    cnt := cnt // this should not be needed, but without it the gen. code is wrong
    io.resetOut := false.B
  }
}


/**
 * Top level to connect the button to the reset.
 */
class KnightTop extends Module {
  val io = IO(new Bundle {
    val btn = Input(UInt(4.W))
    val led = Output(Bits(6.W))
  })

  // Invert the reset button and two flip-flop input synchronization
  val manReset = (~io.btn(3)).toBool
  val syncBtn = RegNext(RegNext(manReset))

  val resGen = Module(new ResetGen())

  // Manual or generated reset
  val resetVal = syncBtn || resGen.io.resetOut

  // DE2-115 has a 50 MHz clock
  val knight = Module(new KnightRider(resetVal, 50000000))

  io.led <> knight.io.led
}

object KnightMain extends App {
  chisel3.Driver.execute(Array("--target-dir", "generated"), () => new KnightTop())
}