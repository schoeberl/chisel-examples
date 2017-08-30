/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * An ALU is a minimal start for a processor.
 * 
 */

package simple

import Chisel._


/**
 * Test the Alu design
 */
class AluTester(dut: Alu) extends Tester(dut) {

  // This is exhaustive testing, which usually is not possible
  for (a <- 0 to 15) {
    for (b <- 0 to 15) {
      for (op <- 0 to 3) {
        val result =
          op match {
            case 0 => a + b
            case 1 => a - b
            case 2 => a | b
            case 3 => a & b
          }
        val resMask = result & 0x0f

        poke(dut.io.fn, op)
        poke(dut.io.a, a)
        poke(dut.io.b, b)
        step(1)
        expect(dut.io.result, resMask)
      }
    }
  }
}

object AluTester {
  def main(args: Array[String]): Unit = {
    println("Testing the ALU")
    chiselMainTest(Array("--genHarness", "--test", "--backend", "c",
      "--compile", "--targetDir", "generated"),
      () => Module(new Alu())) {
        f => new AluTester(f)
      }
  }
}
