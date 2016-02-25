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

/**
 * This is a minimal AXI style data plus handshake channel.
 */
class Channel extends Bundle {
  val data = Bits(INPUT, 8)
  val ready = Bool(OUTPUT)
  val valid = Bool(INPUT)
}

class Buffer extends Module {
  val io = new Bundle {
    val in = new Channel()
    val out = new Channel().flip
  }

  io.in <> io.out
}

/**
 * Transmit part of the UART.
 * A minimal version without any additional buffering.
 * Use an AXI like valid/ready handshake.
 */
class Tx(frequency: Int, baudRate: Int) extends Module {
  val io = new Bundle {
    val txd = Bits(OUTPUT, 1)
    val channel = new Channel()
  }

  val BIT_CNT = UInt((frequency + baudRate / 2) / baudRate - 1)

  val shiftReg = Reg(init = Bits(0x3f))
  val cntReg = Reg(init = UInt(0, 20))
  val bitsReg = Reg(init = UInt(0, 4))

  io.channel.ready := (cntReg === UInt(0)) && (bitsReg === UInt(0))
  io.txd := shiftReg(0)

  when(cntReg === UInt(0)) {

    cntReg := UInt(BIT_CNT)
    when(bitsReg =/= UInt(0)) {
      val shift = shiftReg >> 1
      shiftReg := Cat(Bits(1), shift(9, 0))
      bitsReg := bitsReg - UInt(1)
    }.otherwise {
      when(io.channel.valid) {
        shiftReg(0) := Bits(0) // start bit
        shiftReg(8, 1) := io.channel.data // data
        shiftReg(10, 9) := Bits(3) // two stop bits
        bitsReg := UInt(11)
      }.otherwise {
        shiftReg := Bits(0x3f)
      }
    }

  }.otherwise {
    cntReg := cntReg - UInt(1)
  }

  debug(shiftReg)
}

/**
 * A transmitter with a single buffer.
 */
class BufferedTx(frequency: Int, baudRate: Int) extends Module {
  val io = new Bundle {
    val txd = Bits(OUTPUT, 1)
    val channel = new Channel()
  }
  val tx = Module(new Tx(frequency, baudRate))
  val buf = Module(new Buffer())
  
  buf.io.in <> io.channel 
  tx.io.channel <> buf.io.out
  io.txd <> tx.io.txd
}


/**
 * A basic UART.
 */
class Uart extends Module {
  val io = new Bundle {
    val rxd = Bits(INPUT, 1)
    val txd = Bits(OUTPUT, 1)
  }

  // input synchronization
  val regSyn1 = Reg(next = io.rxd)
  val regSyn2 = Reg(next = regSyn1)
  // just echo to check the pin assignment
  io.txd := regSyn2

}

object UartMain {
  def main(args: Array[String]): Unit = {
    println("Hello from UART")
    chiselMain(args, () => Module(new Uart()))
  }
}

class UartTester(dut: Tx) extends Tester(dut) {

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

object UartTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--backend", "c", "--compile", "--test",
      "--genHarness", "--vcd", "--targetDir", "generated"),
      () => Module(new Tx(10000, 3000))) {
        c => new UartTester(c)
      }
  }
}