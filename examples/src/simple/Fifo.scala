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

class EnqIO(size: Int) extends Bundle {
  val din = UInt(INPUT, size)
  val write = Bool(INPUT)
  val ready = Bool(OUTPUT) // or use full
}
class DeqIO(size: Int) extends Bundle {
  val dout = UInt(OUTPUT, size)
  val read = Bool(INPUT)
  val valid = Bool(OUTPUT)
}

class FifoRegister(size: Int) extends Module {
  val io = new Bundle {
    val enq = new EnqIO(size)
    val deq = new DeqIO(size)
  }

  val empty :: full :: Nil = Enum(UInt(), 2)
  val stateReg = Reg(init = empty)
  // TODO: maybe just specify the size and not a reset value
  val dataReg = Reg(init = Bits(0, size))

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

  io.enq.ready := (stateReg === empty)
  io.deq.valid := (stateReg === full)
  io.deq.dout := dataReg
}
/**
 * This is bubble FIFO, but we change the name to keep the BubbleFifo
 * example till the Chisel VCD output is fixed.
 */
class Fifo(size: Int, depth: Int) extends Module {
  val io = new Bundle {
    val enq = new EnqIO(size)
    val deq = new DeqIO(size)
  }

  val stage = Module(new FifoRegister(size))
  // could we simple use Scala arrays in the following?
  // val buffers = Vec.fill(depth) {new FifoRegister(size) }
  // We need to use Scala arrays! Vec is only for Data aggregate.
  val buffers = Array.fill(depth) { Module(new FifoRegister(size)) }
  for (i <- 0 until depth - 1) {
    buffers(i).io.deq.dout <> buffers(i + 1).io.enq.din
    buffers(i).io.deq.valid <> buffers(i + 1).io.enq.write
    buffers(i).io.deq.read <> buffers(i + 1).io.enq.ready
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
  peek(dut.io.enq.ready)

  // write into the buffer
  poke(dut.io.enq.din, 0x12)
  poke(dut.io.enq.write, 1)
  step(1)
  peek(dut.io.enq.ready)

  poke(dut.io.enq.din, 0x34)
  poke(dut.io.enq.write, 0)
  step(1)
  peek(dut.io.enq.ready)

  // read out
  poke(dut.io.deq.read, 1)
  step(1)
  poke(dut.io.deq.read, 0)
  step(1)

  // write next
  poke(dut.io.enq.din, 0x56)
  poke(dut.io.enq.write, 1)
  step(1)
  peek(dut.io.enq.ready)
  
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
