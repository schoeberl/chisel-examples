/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Play with FIFO buffers.
 * 
 */

import Chisel._
import Node._
import scala.collection.mutable.HashMap

/*
 * On signal naming:
 * 
 * Alter's FIFO component:
 * 
 * data - data in, q - data out, wrreq and rdreq
 * state: full and empty
 * 
 * Xilinx's FIFO component:
 * din and dout, wr_en, rd_en
 * state: full and empty
 * 
 */

class WriterIO(size: Int) extends Bundle {
  val write = Bool(INPUT)
  val full = Bool(OUTPUT)
  val din = UInt(INPUT, size)
}

class ReaderIO(size: Int) extends Bundle {
  val read = Bool(INPUT)
  val empty = Bool(OUTPUT)
  val dout = UInt(OUTPUT, size)
}

class FifoRegister(size: Int) extends Module {
  val io = new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  }

  val empty :: full :: Nil = Enum(UInt(), 2)
  val stateReg = Reg(init = empty)
  val dataReg = Reg(init = Bits(0, size))

  when(stateReg === empty) {
    when(io.enq.write) {
      stateReg := full
      dataReg := io.enq.din
    }
  }.elsewhen(stateReg === full) {
    when(io.deq.read) {
      stateReg := empty
      dataReg := Bits(0) // just to better see empty slots in the waveform
    }
  }.otherwise {
    // There should not be an otherwise state
  }

  io.enq.full := (stateReg === full)
  io.deq.empty := (stateReg === empty)
  io.deq.dout := dataReg
}
/**
 * This is bubble FIFO, but we change the name to keep the BubbleFifo
 * example till the Chisel VCD output is fixed.
 */
class Fifo(size: Int, depth: Int) extends Module {
  val io = new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  }

  val stage = Module(new FifoRegister(size))
  // could we simple use Scala arrays in the following?
  // val buffers = Vec.fill(depth) {new FifoRegister(size) }
  // We need to use Scala arrays! Vec is only for Data aggregate.
  val buffers = Array.fill(depth) { Module(new FifoRegister(size)) }
  for (i <- 0 until depth - 1) {
    buffers(i + 1).io.enq.din := buffers(i).io.deq.dout
    buffers(i + 1).io.enq.write := ~buffers(i).io.deq.empty
    buffers(i).io.deq.read := ~buffers(i + 1).io.enq.full
  }
  io.enq <> buffers(0).io.enq
  io.deq <> buffers(depth - 1).io.deq
}

/**
 * Test the design.
 */
class FifoTester(dut: Fifo) extends Tester(dut) {

  // some defaults for all signals
  poke(dut.io.enq.din, 0xab)
  poke(dut.io.enq.write, 0)
  poke(dut.io.deq.read, 0)
  step(1)
  peek(dut.io.enq.full)

  // write into the buffer
  poke(dut.io.enq.din, 0x12)
  poke(dut.io.enq.write, 1)
  step(1)
  peek(dut.io.enq.full)

  poke(dut.io.enq.din, 0x34)
  poke(dut.io.enq.write, 0)
  step(1)
  peek(dut.io.enq.full)

  // read out
  poke(dut.io.deq.read, 1)
  step(1)
  poke(dut.io.deq.read, 0)
  step(1)

  // write next
  poke(dut.io.enq.din, 0x56)
  poke(dut.io.enq.write, 1)
  step(1)
  peek(dut.io.enq.full)
  
  for (i <- 0 until 20)
    step(1)
}

object FifoTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(args, () => Module(new Fifo(8, 4))) {
      f => new FifoTester(f)
    }
  }
}
