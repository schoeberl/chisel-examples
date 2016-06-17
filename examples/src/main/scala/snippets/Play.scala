/*
 * Copyright: 2015, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Code snippets for Chisel slides.
 * Does not perform any useful function.
 * 
 */

package snippets

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

class AluFields extends Bundle {
  val function = UInt(2)
  val inputA = UInt(8)
  val inputB = UInt(8)
  val result = UInt(8)
}

class AluIO extends Bundle {
  val function = UInt(INPUT, 2)
  val inputA = UInt(INPUT, 8)
  val inputB = UInt(INPUT, 8)
  val result = UInt(OUTPUT, 8)
}

class Channel extends Bundle {
  val data = UInt(INPUT, 32)
  val ready = Bool(OUTPUT)
  val valid = Bool(INPUT)
}

class ParamChannel(n: Int) extends Bundle {
  val data = UInt(INPUT, n)
  val ready = Bool(OUTPUT)
  val valid = Bool(INPUT)
}

class ChannelUsage extends Bundle {
  val input = new Channel()
  val output = new Channel().flip()
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

class ParamAdder(n: Int) extends Module {
  val io = new Bundle {
    val a = UInt(INPUT, n)
    val b = UInt(INPUT, n)
    val result = UInt(OUTPUT, n)
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

class Count extends Module {
  val io = new Bundle {
    val cnt = UInt(OUTPUT, 8)
  }

  val cntReg = Reg(init = UInt(0, 8))

  //cntReg := Mux(cntReg === UInt(100), UInt(0), cntReg + UInt(1))

  cntReg := cntReg + UInt(1)
  when(cntReg === UInt(100)) {
    cntReg := UInt(0)
  }

  io.cnt := cntReg
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

  val condition = cond
  val trueVal = a
  val falseVal = b

  val selection = Mux(cond, trueVal, falseVal)

  (a | b) & ~(c ^ d)

  val c1 = Bool(true)
  val c2 = Bool(false)
  val c3 = Bool(false)

  val v = UInt(5)
  when(condition) {
    v := UInt(0)
  }

  when(c1) { v := UInt(1) }
  when(c2) { v := UInt(2) }

  when(c1) { v := UInt(1) }
    .elsewhen(c2) { v := UInt(2) }
    .otherwise { v := UInt(3) }

  val latch = UInt(width = 5)
  when(cond) {
    latch := UInt(3)
  }

  def addSub(add: Bool, a: UInt, b: UInt) =
    Mux(add, a + b, a - b)

  val res = addSub(cond, a, b)

  def rising(d: Bool) = d && !Reg(next = d)

  val edge = rising(cond)

  val risingEdge = d & !Reg(next = d)

  def counter(n: UInt) = {

    val cntReg = Reg(init = UInt(0, 8))

    cntReg := cntReg + UInt(1)
    when(cntReg === n) {
      cntReg := UInt(0)
    }
    cntReg
  }

  val conter100 = counter(UInt(100))

  val myVec = Vec.fill(3) { SInt(width = 10) }
  val y = myVec(2)
  myVec(0) := SInt(-3)

  val ch32 = new ParamChannel(32)
  val add8 = Module(new ParamAdder(8))

  val inVal = UInt(0)

  val shiftReg = Reg(init = UInt(0, 8))

  shiftReg(0) := inVal

  for (i <- 1 until 8) {
    shiftReg(i) := shiftReg(i - 1)
  }

  val useA = false

  class Base extends Module { val io = new Bundle() }
  class VariantA extends Base { }
  class VariantB extends Base { }

  val m = if (useA) Module(new VariantA())
  else Module(new VariantB())
}
/**
 * A simple, configurable counter that wraps around.
 */
class Play(size: Int) extends Module {
  val io = new Bundle {
    val out = UInt(OUTPUT, size)
    val a = UInt(INPUT, 4)
    val b = UInt(INPUT, 4)
    val c = UInt(INPUT, 4)
    val d = UInt(INPUT, 4)
    val result = UInt(OUTPUT, 4)
  }

  val r1 = Reg(init = UInt(0, size))
  r1 := r1 + UInt(1)

  val nextVal = r1
  val r = Reg(next = nextVal)

  val initReg = Reg(init = UInt(0, 8))
  initReg := initReg + UInt(1)

  printf("Counting %x\n", r1)
  //  println()

  val a = io.a
  val b = io.b
  val c = io.c
  val d = io.d

  val addVal = a + b
  val orVal = a | b
  val boolVal = a >= b

  def adder(v1: UInt, v2: UInt) = v1 + v2

  val add1 = adder(a, b)
  val add2 = adder(c, d)

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

  //  step(10)
  for (i <- 0 until 5) {
    println(i)
    println(peek(c.io.out))
    step(1)
  }

  for (i <- 0 until 5) {
    println(i)
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

class AdderTester(dut: Adder) extends Tester(dut) {

  // Set input values
  poke(dut.io.a, 3)
  poke(dut.io.b, 4)
  // Execute one iteration
  step(1)
  // Print the result
  val res = peek(dut.io.result)
  println(res)

  // Or compare against expected value
  expect(dut.io.result, 7)
}

/**
 * Create a counter and a tester.
 */
object AdderTester {
  def main(args: Array[String]): Unit = {
    chiselMainTest(Array("--genHarness", "--test", "--backend", "c",
      "--compile", "--targetDir", "generated"),
      () => Module(new Adder())) {
        c => new AdderTester(c)
      }
  }
}

// A simple class
class Example {
  // A field, initialized in the constructor
  var n = 0

  // A setter method
  def set(v: Integer) {
    n = v
  }

  // Another method
  def print() {
    println(n)
  }

  def foo() {

    // Value is a constant
    val i = 0
    // No new assignment; will not compile
    //i = 3

    // Variable can change the value
    var v = "Hello"
    v = "Hello World"

    // Type usually inferred, but can be declared
    var s: String = "abc"
  }

  def bar() {

    // Loops from 0 to 9
    // Automatically creates loop value i
    for (i <- 0 until 10) {
      println(i)
    }

    for (i <- 0 until 10) {
      if (i % 2 == 0) {
        println(i + " is even")
      } else {
        println(i + " is odd")
      }
    }

    // An integer array with 10 elements
    val numbers = new Array[Integer](10)
    for (i <- 0 until numbers.length) {
      numbers(i) = i * 10
    }
    println(numbers(9))

    // List of integers
    val list = List(1, 2, 3)
    println(list)
    // Different form of list construction
    val listenum = 'a' :: 'b' :: 'c' :: Nil
    println(listenum)

  }
}

// A singleton object
object Example {

  // The start of a Scala program
  def main(args: Array[String]): Unit = {

    val e = new Example()
    e.print()
    e.set(42)
    e.print()

    e.bar()
  }
}
