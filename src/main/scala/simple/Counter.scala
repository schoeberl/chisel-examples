/*
 * Copyright: 2015, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * A simple counter example with configurable bit width and with a test bench.
 * 
 */

package simple

import chisel3._

/**
 * A simple, configurable counter that wraps around.
 */
class Counter(size: Int) extends Module {
  val io = IO(new Bundle {
    val out = Output(UInt(size.W))
  })

  val r1 = RegInit(0.U(size.W))
  r1 := r1 + 1.U

  io.out := r1
}
