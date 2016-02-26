/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Play with FIFO buffers.
 * 
 */

package simple

import Chisel._

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

/**
 * A single register (=stage) to build the FIFO.
 */
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
 * This is a bubble FIFO.
 */
class BubbleFifo(size: Int, depth: Int) extends Module {
  val io = new Bundle {
    val enq = new WriterIO(size)
    val deq = new ReaderIO(size)
  }

  val stage = Module(new FifoRegister(size))
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
class FifoTester(dut: BubbleFifo) extends Tester(dut) {

  // some defaults for all signals
  poke(dut.io.enq.din, 0xab)
  poke(dut.io.enq.write, 0)
  poke(dut.io.deq.read, 0)
  step(1)
  var full = peek(dut.io.enq.full)
  var empty = peek(dut.io.deq.empty)

  // write into the buffer
  poke(dut.io.enq.din, 0x12)
  poke(dut.io.enq.write, 1)
  step(1)
  full = peek(dut.io.enq.full)

  poke(dut.io.enq.din, 0xff)
  poke(dut.io.enq.write, 0)
  step(1)
  full = peek(dut.io.enq.full)

  step(3) // see the bubbling of the first element

  // Fill the whole buffer with a check for full condition
  // Only every second cycle a write can happen.
  for (i <- 0 until 7) {
    full = peek(dut.io.enq.full)
    poke(dut.io.enq.din, 0x80 + i)
    if (full == 0) {
      poke(dut.io.enq.write, 1)
    } else {
      poke(dut.io.enq.write, 0)
    }
    step(1)
  }

  // Now we know it is full, so do a single read and watch
  // how this empty slot bubble up to the FIFO input.
  poke(dut.io.deq.read, 1)
  step(1)
  poke(dut.io.deq.read, 0)
  step(6)

  // New read out the whole buffer.
  // Also watch that maximum read out is every second clock cycle
  for (i <- 0 until 7) {
    empty = peek(dut.io.deq.empty)
    if (empty == 0) {
      poke(dut.io.deq.read, 1)
    } else {
      poke(dut.io.deq.read, 0)
    }
    step(1)
  }

  // Now write and read at maximum speed for some time
  for (i <- 1 until 16) {
    full = peek(dut.io.enq.full)
    poke(dut.io.enq.din, i)
    if (full == 0) {
      poke(dut.io.enq.write, 1)
    } else {
      poke(dut.io.enq.write, 0)
    }
    empty = peek(dut.io.deq.empty)
    if (empty == 0) {
      poke(dut.io.deq.read, 1)
    } else {
      poke(dut.io.deq.read, 0)
    }
    step(1)
  }

}

object FifoTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array[String]("--genHarness", "--test", "--backend", "c",
      "--compile", "--vcd", "--targetDir", "generated"),
      () => Module(new BubbleFifo(8, 4))) {
        f => new FifoTester(f)
      }
  }
}
