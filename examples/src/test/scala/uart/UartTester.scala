/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * A UART is a serial port, also called an RS232 interface.
 * 
 */

package uart

import Chisel._

class TxTester(dut: Tx) extends Tester(dut) {

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

object TxTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--backend", "c", "--compile", "--test",
      "--genHarness", "--vcd", "--targetDir", "generated"),
      () => Module(new Tx(10000, 3000))) {
        c => new TxTester(c)
      }
  }
}

class BufferedTxTester(dut: BufferedTx) extends Tester(dut) {

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

object BufferedTxTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--backend", "c", "--compile", "--test",
      "--genHarness", "--vcd", "--targetDir", "generated"),
      () => Module(new BufferedTx(10000, 3000))) {
        c => new BufferedTxTester(c)
      }
  }
}

class SenderTester(dut: Sender) extends Tester(dut) {

  step(300)
}



object SenderTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--backend", "v", "--compile", "--test",
      "--genHarness", "--vcd", "--targetDir", "generated"),
      () => Module(new Sender(10000, 3000))) {
        c => new SenderTester(c)
      }
  }
}

class RxTester(dut: Rx) extends Tester(dut) {

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

object RxTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--backend", "c", "--compile", "--test",
      "--genHarness", "--vcd", "--targetDir", "generated"),
      () => Module(new Rx(10000, 3000))) {
        c => new RxTester(c)
      }
  }
}
