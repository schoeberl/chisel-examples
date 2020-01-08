package util

import chisel3.iotesters.PeekPokeTester
import chisel3.iotesters.Driver
import org.scalatest._


class DebounceSpec extends FlatSpec with Matchers {
  "Debounce test" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"), () => new Debounce(100)) {
      c => new PeekPokeTester(c) {
        poke(c.io.btnU, 0)
        step(3)
        expect(c.io.led, 0)
        val sample = c.FAC
        step(sample/3)
        poke(c.io.btnU, 1)
        step(sample/30)
        poke(c.io.btnU, 0)
        step(sample/30)
        poke(c.io.btnU, 1)
        step(sample)
        // expect(c.io.led, 1)
        expect(c.io.led, 0)
        step(sample)
        expect(c.io.led, 0)
        step(sample)
      }
    } should be (true)
  }
}
