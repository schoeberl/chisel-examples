package simple

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

/**
 * Test the Alu design
 */

class AluTester extends AnyFlatSpec with ChiselScalatestTester {

  "AluTester test" should "pass" in {
    test(new Alu) { dut =>

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

            dut.io.fn.poke(op.U)
            dut.io.a.poke(a.U)
            dut.io.b.poke(b.U)
            dut.clock.step(1)
            dut.io.result.expect(resMask.U)
          }
        }
      }
    }
  }
}
