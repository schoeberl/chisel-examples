/*
 * Copyright: 2014, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * An ALU is a minimal start for a processor.
 * 
 */

import Chisel._
import Node._
import scala.collection.mutable.HashMap

/**
 * A basic UART.
 */
class Uart extends Module {
  val io = new Bundle {
//    val sw = UInt(INPUT, 10)
//    val led = UInt(OUTPUT, 10)
    val rxd = Bits(INPUT, 1)
    val txd = Bits(OUTPUT, 1)
  }

  // input synchronization
  val regSyn1 = Reg(next = io.rxd)
  val regSyn2 = Reg(next = regSyn1)
  // just echo to check the pin assignment
  io.txd := regSyn2

}

// Generate the Verilog code by invoking chiselMain() in our main()
object UartMain {
  def main(args: Array[String]): Unit = {
    println("Hello from UART")
    chiselMain(args, () => Module(new Uart()))
  }
}