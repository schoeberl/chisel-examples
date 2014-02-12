/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * An ALU is a minimal start for a processor.
 * 
 */

// shall we have the examples in packages?
// package hello


import Chisel._
import Node._


/**
 * This shall become our ALU example
 */
class Alu extends Module {
  val io = new Bundle {
    val led = UInt(OUTPUT, 1)
  }
  val CNT_MAX = UInt(16000000 / 2 - 1);
  val r1 = Reg(init = UInt(0, 25))
  val blk = Reg(init = UInt(0, 1))

  r1 := r1 + UInt(1)
  when(r1 === CNT_MAX) {
    r1 := UInt(0)
    blk := ~blk
  }
  io.led := blk
}

// Generate the Verilog code by invoking chiselMain() in our main()
object AluMain {
  def main(args: Array[String]): Unit = {
    chiselMain(args, () => Module(new Alu()))
  }
}
