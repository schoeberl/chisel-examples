/*
 *
 * An ALU is a minimal start for a processor.
 *
 */

package simple

import chisel3._
import chisel3.util._

/**
 * This is a very basic ALU example.
 */
class Alu extends Module {
  val io = IO(new Bundle {
    val fn = Input(UInt(2.W))
    val a = Input(UInt(4.W))
    val b = Input(UInt(4.W))
    val result = Output(UInt(4.W))
  })

  // Use shorter variable names
  val fn = io.fn
  val a = io.a
  val b = io.b

  val result = Wire(UInt(4.W))
  // some default value is needed
  result := 0.U

  // The ALU selection
  switch(fn) {
    is(0.U) { result := a + b }
    is(1.U) { result := a - b }
    is(2.U) { result := a | b }
    is(3.U) { result := a & b }
  }

  // Output on the LEDs
  io.result := result
}

/**
 * A top level to wire FPGA buttons and LEDs
 * to the ALU input and output.
 */
class AluTop extends Module {
  val io = IO(new Bundle {
    val sw = Input(UInt(10.W))
    val led = Output(UInt(10.W))
  })

  val alu = Module(new Alu())

  // Map switches to the ALU input ports
  alu.io.fn := io.sw(1, 0)
  alu.io.a := io.sw(5, 2)
  alu.io.b := io.sw(9, 6)

  // And the result to the LEDs (with 0 extension)
  io.led := alu.io.result
}

// Generate the Verilog code by invoking the Driver
object AluMain extends App {
  println("Generating the ALU hardware")
  chisel3.Driver.execute(Array("--target-dir", "generated"), () => new AluTop())
}

