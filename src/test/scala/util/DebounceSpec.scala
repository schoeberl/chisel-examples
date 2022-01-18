package util

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class DebounceSpec extends AnyFlatSpec with ChiselScalatestTester {

  "Debounce test" should "pass" in {
    val FAC = 100
    test(new Debounce(FAC)) { dut =>
      dut.io.btnU.poke(false.B)
      dut.clock.step(3)
      dut.io.led.expect(0.U)
      dut.clock.step(FAC/3)
      dut.io.btnU.poke(true.B)
      dut.clock.step(FAC/30)
      dut.io.btnU.poke(false.B)
      dut.clock.step(FAC/30)
      dut.io.btnU.poke(true.B)
      dut.clock.step(FAC)
    }
  }
}
