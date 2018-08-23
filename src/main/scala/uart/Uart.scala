/*
 * Copyright: 2014-2018, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * A UART is a serial port, also called an RS232 interface.
 * 
 */

package uart

import chisel3._
import chisel3.util._

/**
 * This is a minimal AXI style data plus handshake channel.
 */
class Channel extends Bundle {
  val data = Input(Bits(8.W))
  val ready = Output(Bool())
  val valid = Input(Bool())
}

/**
 * Transmit part of the UART.
 * A minimal version without any additional buffering.
 * Use an AXI like valid/ready handshake.
 */
class Tx(frequency: Int, baudRate: Int) extends Module {
  val io = IO(new Bundle {
    val txd = Output(Bits(1.W))
    val channel = new Channel()
  })

  val BIT_CNT = ((frequency + baudRate / 2) / baudRate - 1).asUInt()

  val shiftReg = RegInit(0x7ff.U)
  val cntReg = RegInit(0.U(20.W))
  val bitsReg = RegInit(0.U(4.W))

  io.channel.ready := (cntReg === 0.U) && (bitsReg === 0.U)
  io.txd := shiftReg(0)

  // TODO: make the counter a tick generator
  when(cntReg === 0.U) {

    cntReg := BIT_CNT
    when(bitsReg =/= 0.U) {
      val shift = shiftReg >> 1
      shiftReg := Cat(1.U, shift(9, 0))
      bitsReg := bitsReg - UInt(1)
    }.otherwise {
      when(io.channel.valid) {
        shiftReg := Cat(Cat(3.U, io.channel.data), 0.U) // two stop bits, data, one start bit
//        shiftReg(0) := 0.U // start bit
//        shiftReg(8, 1) := io.channel.data // data
//        shiftReg(10, 9) := 3.U // two stop bits
        bitsReg := 11.U
      }.otherwise {
        shiftReg := 0x7ff.U
      }
    }

  }.otherwise {
    cntReg := cntReg - 1.U
  }

  // debug(shiftReg)
}

/**
 * Receive part of the UART.
 * A minimal version without any additional buffering.
 * Use an AXI like valid/ready handshake.
 *
 * The following code is inspired by Tommy's receive code at:
 * https://github.com/tommythorn/yarvi
 */
class Rx(frequency: Int, baudRate: Int) extends Module {
  val io = IO(new Bundle {
    val rxd = Input(Bits(1.W))
    val channel = new Channel().flip
  })

  val BIT_CNT = UInt((frequency + baudRate / 2) / baudRate - 1)
  val START_CNT = UInt((3 * frequency / 2 + baudRate / 2) / baudRate - 1)

  // Sync in the asynchronous RX data
  val rxReg = Reg(next = Reg(next = io.rxd))

  val shiftReg = Reg(init = Bits("A", 8))
  val cntReg = Reg(init = UInt(0, 20))
  val bitsReg = Reg(init = UInt(0, 4))
  val valReg = Reg(init = Bool(false))

  when(cntReg =/= UInt(0)) {
    cntReg := cntReg - UInt(1)
  }.elsewhen(bitsReg =/= UInt(0)) {
    cntReg := BIT_CNT
    shiftReg := Cat(rxReg, shiftReg >> 1)
    bitsReg := bitsReg - UInt(1)
    // the last shifted in
    when(bitsReg === UInt(1)) {
      valReg := Bool(true)
    }
  }.elsewhen(rxReg === UInt(0)) { // wait 1.5 bits after falling edge of start
    cntReg := START_CNT
    bitsReg := UInt(8)
  }

  when(io.channel.ready) {
    valReg := Bool(false)
  }

  io.channel.data := shiftReg
  io.channel.valid := valReg
}

/**
 * A single byte buffer with an AXI style channel
 */
class Buffer extends Module {
  val io = IO(new Bundle {
    val in = new Channel()
    val out = new Channel().flip
  })

  val empty :: full :: Nil = Enum(UInt(), 2)
  val stateReg = Reg(init = empty)
  val dataReg = Reg(init = Bits(0, 8))

  io.in.ready := stateReg === empty
  io.out.valid := stateReg === full

  when(stateReg === empty) {
    when(io.in.valid) {
      dataReg := io.in.data
      stateReg := full
    }
  }.otherwise { // full, io.out.valid := true
    when(io.out.ready) {
      stateReg := empty
    }
  }
  io.out.data := dataReg
}

/**
 * A transmitter with a single buffer.
 */
class BufferedTx(frequency: Int, baudRate: Int) extends Module {
  val io = IO(new Bundle {
    val txd = Output(Bits(1.W))
    val channel = new Channel()
  })
  val tx = Module(new Tx(frequency, baudRate))
  val buf = Module(new Buffer())

  buf.io.in <> io.channel
  tx.io.channel <> buf.io.out
  io.txd <> tx.io.txd
}

/**
 * Send 'hello'.
 */
class Sender(frequency: Int, baudRate: Int) extends Module {
  val io = IO(new Bundle {
    val txd = Output(Bits(1.W))
  })

  val tx = Module(new BufferedTx(frequency, baudRate))

  io.txd := tx.io.txd

  // This is not super elegant
  // val hello = Array[UInt]("H".U, "e".U, "l".U, "l".U, "o".U)
  // val text = Vec[UInt]("H".U, "e".U, "l".U, "l".U, "o".U)
  val hello = Array[UInt](72.U(8.W))
  val text = Vec(hello) //, "e".U, "l".U, "l".U, "o".U)

  val cntReg = Reg(init = UInt(0, 3))

  tx.io.channel.data := text(cntReg)
  tx.io.channel.valid := cntReg =/= UInt(5)

  when(tx.io.channel.ready && cntReg =/= UInt(5)) {
    cntReg := cntReg + UInt(1)
  }
}

class Echo(frequency: Int, baudRate: Int) extends Module {
  val io = IO(new Bundle {
    val txd = Output(Bits(1.W))
    val rxd = Output(Bits(1.W))
  })
  // io.txd := Reg(next = io.rxd, init = UInt(0))
  val tx = Module(new BufferedTx(frequency, baudRate))
  val rx = Module(new Rx(frequency, baudRate))
  io.txd := tx.io.txd
  rx.io.rxd := io.rxd
  tx.io.channel <> rx.io.channel
  tx.io.channel.valid := Bool(true)
//  tx.io.channel.data := Bits('H')
}

class UartMain(frequency: Int, baudRate: Int) extends Module {
  val io = IO(new Bundle {
    val rxd = Input(Bits(1.W))
    val txd = Output(Bits(1.W))
  })
  
  val u = Module(new Sender(50000000, 115200))
  // val u = Module(new Echo(50000000, 115200))
  io.txd := u.io.txd
  // u.io.rxd := io.rxd
}

object UartMain extends App {
  chisel3.Driver.execute(Array("--target-dir", "generated"), () => new UartMain(50000000, 115200))
}

