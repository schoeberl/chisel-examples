/*
 * Copyright: 2016, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Testing register width inference according to original tutorial code.
 * 
 */

package questions

import Chisel._

/**
 * A simple, configurable counter that wraps around.
 */
class RegSize() extends Module {
  val io = new Bundle {
    val result = UInt(OUTPUT, 4)
  }

  // val reg = Reg(init = UInt(15)) // that works

  // Use an UInt just to size the register fails
  val reg = Reg(UInt(15))
  reg := reg + UInt(1)
  io.result := reg
}

/**
 * Test the counter by printing out the value at each clock cycle.
 */
class RegSizeTester(dut: RegSize) extends Tester(dut) {

  step(5)
}

/**
 * Create a counter and a tester.
 */
object RegSizeTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--backend", "c", "--compile", "--test",
      "--genHarness", "--vcd"),
      () => Module(new RegSize())) {
        c => new RegSizeTester(c)
      }
  }
}

/**
 * Create the counter.
 */
object RegSize {
  def main(args: Array[String]): Unit = {
    chiselMain(Array[String]("--backend", "v"),
      () => Module(new RegSize()))
  }
}
