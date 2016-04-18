/*
 * Copyright: 2015, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Code snippets for the slides.
 * 
 */

package simple

import Chisel._

class AluOp extends Bundle {
  val op = UInt(width = 4)
}

class DecodeExecute extends Bundle {
  val rs1 = UInt(width = 32)
  val rs2 = UInt(width = 32)
  val immVal = UInt(width = 32)
  val aluOp = new AluOp()
}

class ExecuteMemory extends Bundle {
  val abc = new Bool()
}

class ExecuteIO extends Bundle {
  val dec = new DecodeExecute().asInput
  val mem = new ExecuteMemory().asOutput
}

class Adder extends Module {
  val io = new Bundle {
    val a = UInt(INPUT, 4)
    val b = UInt(INPUT, 4)
    val result = UInt(OUTPUT, 4)
  }

  val addVal = io.a + io.b
  io.result := addVal
}

class Decode extends Module {
  val io = new Bundle {
    val toExe = new DecodeExecute().asOutput
  }
}

class Execute extends Module {
  val io = new ExecuteIO()
}

class Memory extends Module {
  val io = new Bundle {
    val fromExe = new ExecuteMemory().asInput
  }
}

class CPU extends Module {
  val io = new Bundle {
    val leds = UInt(OUTPUT, 4)
  }
  
  val dec = Module(new Decode())
  val exe = Module(new Execute())
  val mem = Module(new Memory())
  
  dec.io <> exe.io
  mem.io <> exe.io
  
  val adder = Module(new Adder())
  
  val ina = UInt(width = 4)
  val inb = UInt(width = 4)
  
  adder.io.a := ina
  adder.io.b := inb
  val result = adder.io.result
  
}
/**
 * A simple, configurable counter that wraps around.
 */
class Play(size: Int) extends Module {
  val io = new Bundle {
    val out = UInt(OUTPUT, size)
        val a = UInt(INPUT, 4)
    val b = UInt(INPUT, 4)
    val result = UInt(OUTPUT, 4)
  }


  val r1 = Reg(init = UInt(0, size))
  r1 := r1 + UInt(1)
  
  val nextVal = r1
  val r = Reg(next = nextVal)
  
  val a = io.a
  val b = io.b
  
val addVal = a + b
val orVal = a | b
val boolVal = a === b

  io.out := r1
}

/**
 * Test the counter by printing out the value at each clock cycle.
 */
class PlayTester(c: Play) extends Tester(c) {

  for (i <- 0 until 5) {
    println(i)
    println(peek(c.io.out))
    step(1)
  }
}

/**
 * Create a counter and a tester.
 */
object PlayTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array("--genHarness", "--test", "--backend", "c",
      "--compile", "--targetDir", "generated"),
      () => Module(new Play(4))) {
        c => new PlayTester(c)
      }
  }
}
