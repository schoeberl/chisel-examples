/*
 * This code is a minimal hardware described in Chisel.
 * 
 * Copyright: 2013, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Blinking LED: the FPGA version of Hello World
 */

import Chisel._

/**
 * The blinking LED component.
 */

class Hello extends Module {
  val io = new Bundle {
    val led = UInt(OUTPUT, 1)
  }
  val CNT_MAX = UInt(20000000 / 2 - 1);
  
  val cntReg = Reg(init = UInt(0, 25))
  val blkReg = Reg(init = UInt(0, 1))

  cntReg := cntReg + UInt(1)
  when(cntReg === CNT_MAX) {
    cntReg := UInt(0)
    blkReg := ~blkReg
  }
  io.led := blkReg
}

/**
 * An object containing a main() to invoke chiselMain()
 * to generate the Verilog code.
 */
object Hello {
  def main(args: Array[String]): Unit = {
    chiselMain(Array[String]("--backend", "v"), () => Module(new Hello()))
  }
}
