/*
 * Copyright: 2016, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Play with the Altera JTAG communication.
 * This is also a demonstration for using a black box.
 * 
 */

package altera

import Chisel._
import Node._
import scala.collection.mutable.HashMap

class alt_jtag_atlantic extends BlackBox {
  val io = new Bundle {
    val rst_n = UInt(INPUT, 1)
    val r_dat = UInt(INPUT, 8) // data from FPGA
    val r_val = UInt(INPUT, 1) // data valid
    val r_ena = UInt(OUTPUT, 1) // can write (next) cycle, or FIFO not full?
    val t_dat = UInt(OUTPUT, 8) // data to FPGA
    val t_dav = UInt(INPUT, 1) // ready to receive more data
    val t_ena = UInt(OUTPUT, 8) // tx data valid
    val t_pause = UInt(OUTPUT, 8) // ???
  }
  
  setVerilogParameters(new VerilogParameters {
    val INSTANCE_ID = 0
    val LOG2_RXFIFO_DEPTH = 3
    val LOG2_TXFIFO_DEPTH = 3
    val SLD_AUTO_INSTANCE_INDEX = "YES"
  })

  // the clock does not get added to the BlackBox interface by default
  addClock(Driver.implicitClock)

  io.rst_n.setName("rst_n")
  io.r_dat.setName("r_dat")
  io.r_val.setName("r_val")
  io.r_ena.setName("r_ena")
  io.t_dat.setName("t_dat")
  io.t_dav.setName("t_dav")
  io.t_ena.setName("t_ena")
  io.t_pause.setName("t_pause")
}

/**
 * This is the interface to the Altera JTAG stuff.
 * Chisel does not like a module without IO.
 */
class AlteraJtag(resetSignal: Bool = null) extends Module(_reset = resetSignal) {
  val io = new Bundle {
    val led = UInt(OUTPUT, 1)
  }

  val jtag = Module(new alt_jtag_atlantic())

  val isFullReg = Reg(init = Bool(false))
  val dataReg = Reg(init = UInt(0, 8))

  when(!isFullReg) {
    when(jtag.io.t_ena === UInt(1)) {
      dataReg := jtag.io.t_dat + UInt(1)
      isFullReg := Bool(true)
    }
  }.otherwise {
    when(jtag.io.r_ena === UInt(1)) {
      isFullReg := Bool(false)
    }
  }

  jtag.io.t_dav := !isFullReg && (jtag.io.t_ena =/= UInt(1))
  jtag.io.r_val := isFullReg
  jtag.io.r_dat := dataReg
  jtag.io.rst_n := ~reset

  val reg = Reg(next = jtag.io.t_dat)

  // Do some blinking to see the FPGA running
  val CNT_MAX = UInt(24000000 / 2 - 1);
  val r1 = Reg(init = UInt(0, 25))
  val blk = Reg(init = UInt(0, 1))

  r1 := r1 + UInt(1)
  when(r1 === CNT_MAX) {
    r1 := UInt(0)
    blk := ~blk
  }
  io.led := blk
}

class AlteraJtagTop extends Module {
  val io = new Bundle {
    val led = UInt(OUTPUT, 1)
  }

  // don't use a reset for now
  val resetSignal = Bool(false)

  val jtag = Module(new AlteraJtag(resetSignal))
  io <> jtag.io
}

object AlteraJtag {
  def main(args: Array[String]): Unit = {
    chiselMain(Array[String]("--backend", "v", "--targetDir", "generated"),
      () => Module(new AlteraJtagTop()))
  }
}
