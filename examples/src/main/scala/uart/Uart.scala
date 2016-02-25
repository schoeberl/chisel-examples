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
 * Transmit part of the UART.
 * Use an AXI like valid/ready handshake process.
 */
class UartTx(frequency: Int, baudRate: Int) extends Module {
  val io = new Bundle {
    val txd = Bits(OUTPUT, 1)
    val din = Bits(INPUT, 8)
    val ready = Bool(OUTPUT)
    val valid = Bool(INPUT)
  }

  val BIT_CNT = UInt((frequency + baudRate / 2) / baudRate - 1)

  val shiftReg = Reg(init = Bits(0, 1 + 8 + 2))
  val cntReg = Reg(init = UInt(0, 20))
  val bitsReg = Reg(init = UInt(0, 4))

  io.ready := (cntReg === UInt(0)) && (bitsReg === UInt(0))
  io.txd := shiftReg(0)

  when(cntReg === UInt(0)) {

    cntReg := UInt(BIT_CNT)
    when(bitsReg =/= UInt(0)) {
      val shift = shiftReg >> 1
      shiftReg := Cat(Bits(1), shift(9, 0))
      bitsReg := bitsReg - UInt(1)
    }.otherwise {
      when(io.valid) {
        shiftReg(0) := Bits(0) // start bit
        shiftReg(8, 1) := io.din // data
        shiftReg(10, 9) := Bits(3) // two stop bits
        bitsReg := UInt(1 + 8 + 2)
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
 * A basic UART.
 */
class Uart extends Module {
  val io = new Bundle {
    //    val sw = UInt(INPUT, 10)
    //    val led = UInt(OUTPUT, 10)
    val rxd = Bits(INPUT, 1)
    val txd = Bits(OUTPUT, 1)
  }

  // input synchronization
  val regSyn1 = Reg(next = io.rxd)
  val regSyn2 = Reg(next = regSyn1)
  // just echo to check the pin assignment
  io.txd := regSyn2

}

// Generate the Verilog code by invoking chiselMain() in our main()
object UartMain {
  def main(args: Array[String]): Unit = {
    println("Hello from UART")
    chiselMain(args, () => Module(new Uart()))
  }
}

class UartTester(dut: UartTx) extends Tester(dut) {

  step(2)
  poke(dut.io.valid, 1)
  poke(dut.io.din, 'A')
  step(4)
  poke(dut.io.valid, 0)
  poke(dut.io.din, 0)
  step(40)
  poke(dut.io.valid, 1)
  poke(dut.io.din, 'B')
  step(4)
  poke(dut.io.valid, 0)
  poke(dut.io.din, 0)
  step(30)
}

object UartTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--backend", "c", "--compile", "--test",
      "--genHarness", "--vcd", "--targetDir", "generated"),
      () => Module(new UartTx(10000, 3000))) {
        c => new UartTester(c)
      }
  }
}