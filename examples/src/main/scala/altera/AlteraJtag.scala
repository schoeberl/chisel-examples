/*
 * Copyright: 2016, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Use the Altera JTAG communication and translate the Atlantic
 * interface to a ready/go interface (like AXI).
 * 
 * This is also a demonstration for using a black box.
 */

package altera

import Chisel._
import Node._
import scala.collection.mutable.HashMap

/**
 * The Altera JTAG 'UART' with an Atlantic interface and no documentation.
 */
class alt_jtag_atlantic extends BlackBox {
  val io = new Bundle {
    val rst_n = UInt(INPUT, 1)
    val r_dat = UInt(INPUT, 8) // data from FPGA to host
    val r_val = UInt(INPUT, 1) // data valid
    val r_ena = UInt(OUTPUT, 1) // can write (next) cycle, or FIFO not full?
    val t_dat = UInt(OUTPUT, 8) // data to FPGA
    val t_dav = UInt(INPUT, 1) // ready to receive more data
    val t_ena = UInt(OUTPUT, 1) // tx data valid (tx form host view)
    val t_pause = UInt(OUTPUT, 1) // ???
  }

  setVerilogParameters(new VerilogParameters {
    val INSTANCE_ID = 0
    val LOG2_RXFIFO_DEPTH = 3
    val LOG2_TXFIFO_DEPTH = 3
    val SLD_AUTO_INSTANCE_INDEX = "YES"
  })

  // the clock does not get added to the BlackBox interface by default
  addClock(Driver.implicitClock)

  io.rst_n.setName("rst_n")
  io.r_dat.setName("r_dat")
  io.r_val.setName("r_val")
  io.r_ena.setName("r_ena")
  io.t_dat.setName("t_dat")
  io.t_dav.setName("t_dav")
  io.t_ena.setName("t_ena")
  io.t_pause.setName("t_pause")
}

/**
 * Wrap the Altera Atlantic JTAG component into a nicer Chisel module.
 * And translate this (strange) Atlantic interface to something more AXI like.
 *
 * See: https://www.altera.com/content/dam/altera-www/global/en_US/pdfs/literature/fs/fs_atlantic.pdf
 *
 * Altera talks about "Slave sink indicates it has space for threshold words”.
 * Could the jtag_atlantic spill out up to 2^LOG2_RXFIFO_DEPTH for a single
 * dav (rx_ready)?
 *
 */

class AlteraJtag() extends Module {
  val io = new Bundle {
    val txReady = Bool(OUTPUT) // can write (next) cycle
    val txValid = Bool(INPUT)  // data valid = single cycle write
    val txData = UInt(INPUT, 8) // data from FPGA to host

    val rxData = UInt(OUTPUT, 8) // data from host to FPGA
    val rxReady = Bool(INPUT) // ready to receive more data
    val rxValid = Bool(OUTPUT) // rx data valid

  }
  val alt_jtag = Module(new alt_jtag_atlantic())

  alt_jtag.io.rst_n := ~reset

  // Altera Atlantic interface, mapped to nicer names and some translation.
  // Inspired by Tommy and the AXI interface.

  // Transmitting from FPGA to host. Assuming host is master, FPGA slave.
  // 
  // Register valid and data on behalf of the user of AlteraJtag.
  // Accept one cycle delay - not an issue here.
  //
  // clock             /--\__/--\__/--\__/--\__/--\__/--\__/--\__
  //
  // M: ena, txReady:  _______/-----???????___________/----------
  //
  // S: txValid     :  _____________/-----\______________________
  // S: txData      :  XXXXXXXXXXXXXX Dat XXXXXXXXXXXXXXXXXXXXXXX
  // translated to
  // S: val         :  ___________________/----------------\_____
  // S: dat         :   XXXXXXXXXXXXXXXXXXX Data           XXXXXX
  //
  // Master requests new data with ena.
  // Slave holds data until master requests new data.
  // Latch it here.
  //

  io.txReady := alt_jtag.io.r_ena

  val validReg = Reg(init = Bool(false))
  val dataReg = Reg(init = UInt(0, 8))
  
  // latch valid and data on a txValid
  when(io.txValid) {
    validReg := Bool(true)
    dataReg := io.txData
  }.otherwise {
    // reset valid on new data request with ena
    when(alt_jtag.io.r_ena === UInt(1)) {
      validReg := Bool(false)
    }

  }
  alt_jtag.io.r_val := validReg
  alt_jtag.io.r_dat := dataReg

  // Receiving from host. Host is master, FPGA slave
  //
  // clock             /--\__/--\__/--\__/--\__/--\__/--\__
  //
  // S: dav, rxReady:  _______/-----------\________________
  //
  // M: ena, rxValid:  _____________/-----------\__________
  //
  // M: dat, rxData:   XXXXXXXXXXXXXX Dat X Dat XXXXXXXX
  //
  // Slave indicates with dav for space up to 'threshold' words.
  // Master start sending data (ena, data).
  // Slave indicates with dav low not having 'threshold' words space.
  //
  // Maybe rxValid should be called read
  
  alt_jtag.io.t_dav := io.rxReady
  io.rxValid := alt_jtag.io.t_ena
  io.rxData := alt_jtag.io.t_dat
}

/**
 * This is a simple echo component to test the Altera JTAG communication.
 * Echos a character incremented by 1, i.e., 'a' => 'b'
 * Blink the LED on a character received.
 */
class AlteraJtagEcho(resetSignal: Bool = null) extends Module(_reset = resetSignal) {
  val io = new Bundle {
    val led = UInt(OUTPUT, 1)
  }

  val jtag = Module(new AlteraJtag())

  val isFullReg = Reg(init = Bool(false))
  val dataReg = Reg(init = UInt(0, 8))

  when(!isFullReg) {
    when(jtag.io.rxValid) {
      dataReg := jtag.io.rxData + UInt(1)
      isFullReg := Bool(true)
    }
  }.otherwise {
    when(jtag.io.txReady) {
      isFullReg := Bool(false)
    }
  }

  jtag.io.txData := dataReg
  jtag.io.txValid := isFullReg && jtag.io.txReady

  jtag.io.rxReady := !isFullReg

  // blink on receiving a character
  val blink = Reg(init = UInt(0, 1))
  when(!isFullReg && jtag.io.rxValid === UInt(1)) {
    blink := ~blink;
  }
  io.led := blink
}

/**
 * A top module to set reset to 0.
 */
class AlteraJtagEchoTop extends Module {
  val io = new Bundle {
    val led = UInt(OUTPUT, 1)
  }

  // don't use a reset for now
  val resetSignal = Bool(false)

  val jtag = Module(new AlteraJtagEcho(resetSignal))
  io <> jtag.io
}

object AlteraJtagEcho {
  def main(args: Array[String]): Unit = {
    chiselMain(Array("--backend", "v", "--targetDir", "generated"),
      () => Module(new AlteraJtagEchoTop()))
  }
}
