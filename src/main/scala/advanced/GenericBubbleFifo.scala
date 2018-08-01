/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 *
 * A generic version of the FIFO.
 *
 */

package advanced

import Chisel._

class GenericWriterIO[T <: Data](gen: T) extends Bundle {
  val write = Input(Bool())
  val full = Output(Bool())
  val din = Input(gen.cloneType)
}

class GenericReaderIO[T <: Data](gen: T) extends Bundle {
  val read = Input(Bool())
  val empty = Output(Bool())
  val dout = Output(gen.cloneType)
}

/**
 * A single register (=stage) to build the FIFO.
 */


class GenericFifoRegister[T <: Data](gen: T) extends Module {
  val io = new Bundle {
    val enq = new GenericWriterIO(gen)
    val deq = new GenericReaderIO(gen)
  }

  val empty :: full :: Nil = Enum(UInt(), 2)
  val stateReg = RegInit(empty)
  val dataReg = Reg(gen)

  when(stateReg === empty) {
    when(io.enq.write) {
      stateReg := full
      dataReg := io.enq.din
    }
  }.elsewhen(stateReg === full) {
    when(io.deq.read) {
      stateReg := empty
    }
  }.otherwise {
    // There should not be an otherwise state
  }

  io.enq.full := (stateReg === full)
  io.deq.empty := (stateReg === empty)
  io.deq.dout := dataReg
}

/**
 * This is a generic version of a bubble FIFO.
 */
class GenericBubbleFifo[T <: Data](gen: T, depth: Int) extends Module {
  val io = new Bundle {
    val enq = new GenericWriterIO(gen)
    val deq = new GenericReaderIO(gen)
  }

  private val buffers = Array.fill(depth) { Module(new GenericFifoRegister(gen)) }
  for (i <- 0 until depth - 1) {
    buffers(i + 1).io.enq.din := buffers(i).io.deq.dout
    buffers(i + 1).io.enq.write := ~buffers(i).io.deq.empty
    buffers(i).io.deq.read := ~buffers(i + 1).io.enq.full
  }
  io.enq <> buffers(0).io.enq
  io.deq <> buffers(depth - 1).io.deq
}
