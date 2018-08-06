/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * A UART is a serial port, also called an RS232 interface.
 * 
 */

package uart

import chisel3._
import chisel3.iotesters.PeekPokeTester

class TxTester(dut: Tx) extends PeekPokeTester(dut) {

  step(2)
  poke(dut.io.channel.valid, 1)
  poke(dut.io.channel.data, 'A')
  step(4)
  poke(dut.io.channel.valid, 0)
  poke(dut.io.channel.data, 0)
  step(40)
  poke(dut.io.channel.valid, 1)
  poke(dut.io.channel.data, 'B')
  step(4)
  poke(dut.io.channel.valid, 0)
  poke(dut.io.channel.data, 0)
  step(30)
}

object TxTester extends App {
  iotesters.Driver.execute(Array("--fint-write-vcd"), () => new Tx(10000, 3000)) {
    c => new TxTester(c)
  }
}

class BufferedTxTester(dut: BufferedTx) extends PeekPokeTester(dut) {

  step(2)
  poke(dut.io.channel.valid, 1)
  poke(dut.io.channel.data, 'A')
  // now we have a buffer, keep valid only a single cycle
  step(1)
  poke(dut.io.channel.valid, 0)
  poke(dut.io.channel.data, 0)
  step(40)
  poke(dut.io.channel.valid, 1)
  poke(dut.io.channel.data, 'B')
  step(1)
  poke(dut.io.channel.valid, 0)
  poke(dut.io.channel.data, 0)
  step(30)
}

object BufferedTxTester extends App {
  iotesters.Driver.execute(Array("--fint-write-vcd"), () => new BufferedTx(10000, 3000)) {
    c => new BufferedTxTester(c)
  }
}

class SenderTester(dut: Sender) extends PeekPokeTester(dut) {

  step(300)
}



object SenderTester extends App {
  iotesters.Driver.execute(Array("--fint-write-vcd"), () => new Sender(10000, 3000)) {
    c => new SenderTester(c)
  }
}

class RxTester(dut: Rx) extends PeekPokeTester(dut) {

  poke(dut.io.rxd, 1)
  step(10)
  poke(dut.io.rxd, 0)
  step(10)
  poke(dut.io.rxd, 1)
  step(30)
  poke(dut.io.channel.ready, 1)
  step(1)
  poke(dut.io.channel.ready, 0)
  step(4)
  poke(dut.io.rxd, 0)
  step(10)
  poke(dut.io.rxd, 1)
  step(30)
}

object RxTester extends App {
  iotesters.Driver.execute(Array("--fint-write-vcd"), () => new Rx(10000, 3000)) {
    c => new RxTester(c)
  }
}
