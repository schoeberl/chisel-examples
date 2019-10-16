/*
 * Test the UART.
 * 
 */

package uart

import chisel3._
import chisel3.iotesters.PeekPokeTester

class TxTester(dut: Tx) extends PeekPokeTester(dut) {

  step(2)
  // ready/valid handshake the first character
  poke(dut.io.channel.valid, 1)
  poke(dut.io.channel.bits, 'a')
  while (peek(dut.io.channel.ready) == 0) {
    step(1)
  }
  step(1)
  poke(dut.io.channel.valid, 0)
  poke(dut.io.channel.bits, 0)

  // wait for start bit
  while (peek(dut.io.txd) != 0) {
    step(1)
  }
  // to the first bit
  step(3)

  for (i <- 0 until 8) {
    expect(dut.io.txd, (('A'.toInt >> i) & 0x01))
    step(3)
  }
  // stop bit
  expect(dut.io.txd, 1)
}

object TxTester extends App {
  iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new Tx(10000, 3000)) {
    c => new TxTester(c)
  }
}


class SenderTester(dut: Sender) extends PeekPokeTester(dut) {

  step(300)
}

object SenderTester extends App {
  iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new Sender(10000, 3000)) {
    c => new SenderTester(c)
  }
}

class RxTester(dut: Rx) extends PeekPokeTester(dut) {

  poke(dut.io.rxd, 1)
  step(10)
  // start bit
  poke(dut.io.rxd, 0)
  step(3)
  // 8 data bits
  for (i <- 0 until 8) {
    poke(dut.io.rxd, (0xa5.toInt >> i) & 0x01)
    step(3)
  }
  // stop bit
  poke(dut.io.rxd, 1)
  while(peek(dut.io.channel.valid) == 0) {
    // wait on valid
    step(1)
    println("wait")
  }
  expect(dut.io.channel.bits, 0xa5.toInt)

  // read it out
  poke(dut.io.channel.ready, 1)
  step(1)
  poke(dut.io.channel.ready, 0)
  step(5)

}

object RxTester extends App {
  iotesters.Driver.execute(Array("--target-dir", "generated", "--generate-vcd-output", "on"), () => new Rx(10000, 3000)) {
    c => new RxTester(c)
  }
}
