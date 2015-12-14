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
class BubbleFifo extends Module {
  val io = new Bundle {
    val write = Bool(INPUT)
    val read = Bool(INPUT)
    val ready = Bool(OUTPUT)
    // data is needed
  }

  io.ready := io.write
}


/**
 * Test the design.
 */
class FifoTester(dut: BubbleFifo) extends Tester(dut) {

  // the following feels simply odd in a OO language
  // shouldn't it be Bool(false)?
  poke(dut.io.write, 0)
  step(1)
  peek(dut.io.ready)
  poke(dut.io.write, 1)
  step(1)
  peek(dut.io.ready)
  poke(dut.io.write, 0)
  step(1)
  peek(dut.io.ready)
  
}

object FifoTester {
  def main(args: Array[String]): Unit = {
    println("Testing the ALU")
    chiselMainTest(args, () => Module(new BubbleFifo())) {
      f => new FifoTester(f)
    }
  }
}
