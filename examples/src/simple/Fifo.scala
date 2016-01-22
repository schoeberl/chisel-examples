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

/**
 * This is ?.
 */
class Fifo(size: Int) extends Module {
  val io = new Bundle {
    val din = UInt(INPUT, size)
    val write = Bool(INPUT)
    val read = Bool(INPUT)
    val ready = Bool(OUTPUT)
    val full = Bool(OUTPUT)
    val dout = UInt(OUTPUT, size)
    // just for debugging
    val stateReg = Bool(OUTPUT)
  }

  val empty :: full :: Nil = Enum(UInt(), 2)
  val stateReg = Reg(init = empty)
  // TODO: maybe just specify the size and not a reset value
  val dataReg = Reg(init = Bits(0, size))
  
  when (stateReg === empty) {
    when (io.write) {
      stateReg := full
      dataReg := io.din
    }
  } . elsewhen(stateReg === full) {
    when (io.read) {
      stateReg := empty
    }
  } .otherwise {
    // There should not be an otherwise state
  }

  io.ready := (stateReg === empty)
  io.full := (stateReg === full)
  io.dout := dataReg
  
  io.stateReg := stateReg
}


/**
 * Test the design.
 */
class FifoTester(dut: Fifo) extends Tester(dut) {

  // some defaults for all signals
  poke(dut.io.write, 0)
  poke(dut.io.din, 0xab)
  poke(dut.io.read, 0)
  step(1)
  peek(dut.io.ready)
  
  // write into the buffer
  poke(dut.io.din, 0x12)
  poke(dut.io.write, 1)
  step(1)
  peek(dut.io.ready)
  
  poke(dut.io.din, 0x34)
  poke(dut.io.write, 0)
  step(1)
  peek(dut.io.ready)
  
  // read out
  poke(dut.io.read, 1)
  step(1)
  poke(dut.io.read, 0)
  step(1)
  
  // write next
  poke(dut.io.din, 0x56)
  poke(dut.io.write, 1)
  step(1)
  peek(dut.io.ready)
}

object FifoTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(args, () => Module(new Fifo(8))) {
      f => new FifoTester(f)
    }
  }
}
