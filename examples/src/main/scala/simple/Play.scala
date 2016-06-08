/*
 * Copyright: 2015, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Code snippets for the slides.
 * 
 */

package simple

import scala.io.Source._
import Chisel._

object Helper {

  def fileRead(fileName: String): Vec[Bits] = {
    val source = fromFile(fileName)
    val byteArray = source.map(_.toByte).toArray
    source.close()
    val arr = new Array[Bits](byteArray.length)
    for (i <- 0 until byteArray.length) {
      arr(i) = Bits(byteArray(i), 8)
    }
    val rom = Vec[Bits](arr)
    rom
  }
}

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

  val a = UInt(width = 8)
  val b = UInt(width = 8)
  val d = UInt(width = 8)
  
  val cond = a =/= b

  val c = Mux(cond, a, b)
  
  (a | b) & ~(c ^ d)

  def addSub(add: Bool, a: UInt, b: UInt) =
    Mux(add, a+b, a-b)

  val res = addSub(cond, a, b)
  
  def rising(d: Bool) = d && !Reg(next = d)

  val edge = rising(cond)
  
  val myVec = Vec.fill(3){ SInt(width = 10) }
  val y = myVec(2)
  myVec(0) := SInt(-3)
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
  
  printf("Counting %x\n", r1)

  val a = io.a
  val b = io.b

  val addVal = a + b
  val orVal = a | b
  val boolVal = a >= b

  val cpu = Module(new CPU())
  
  val cores = new Array[Module](32)
  for (j <- 0 until 32)
    cores(j) = Module(new CPU())
    
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
