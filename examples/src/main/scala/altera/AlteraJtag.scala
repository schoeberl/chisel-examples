/*
 * Copyright: 2016, Technical University of Denmark, DTU Compute
 * Author: Martin Schoeberl (martin@jopdesign.com)
 * License: Simplified BSD License
 * 
 * Play with the Altera JTAG communication.
 * This is also a demonstration of using a blak box.
 * 
 */

package altera

import Chisel._
import Node._
import scala.collection.mutable.HashMap

class alt_jtag_atlantic extends BlackBox {
  val io = new Bundle {
    val clk = UInt(INPUT, 1)
    val rst_n = UInt(INPUT, 1)
    val r_dat = UInt(INPUT, 8) // data from FPGA
    val r_val = UInt(INPUT, 1) // data valid
    val r_ena = UInt(OUTPUT, 1) // can write (next) cycle, or FIFO not full?
    val t_dat = UInt(OUTPUT, 8) // data to FPGA
    val t_dav = UInt(INPUT, 1) // ready to receive more data
    val t_ena = UInt(OUTPUT, 8) // tx data valid
    val t_pause = UInt(OUTPUT, 8) // ???
  }

  io.t_dat.setName("t_dat")
}
/**
 * This is a very basic ALU example.
 */
class AlteraJtag extends Module {
  val io = new Bundle {
    val fn = UInt(INPUT, 2)
    val a = UInt(INPUT, 4)
    val b = UInt(INPUT, 4)
    val result = UInt(OUTPUT, 4)
  }

  //	component alt_jtag_atlantic is
  //		generic (
  //			INSTANCE_ID : integer;
  //			LOG2_RXFIFO_DEPTH : integer;
  //			LOG2_TXFIFO_DEPTH : integer;
  //			SLD_AUTO_INSTANCE_INDEX : string
  //		);
  //		port (
  //			clk : in std_logic;
  //			rst_n : in std_logic;
  //			-- the signal names are a little bit strange
  //			r_dat : in std_logic_vector(7 downto 0); -- data from FPGA
  //			r_val : in std_logic; -- data valid
  //			r_ena : out std_logic; -- can write (next) cycle, or FIFO not full?
  //			t_dat : out std_logic_vector(7 downto 0); -- data to FPGA
  //			t_dav : in std_logic; -- ready to receive more data
  //			t_ena : out std_logic; -- tx data valid
  //			t_pause : out std_logic -- ???
  //		);
  //	end component alt_jtag_atlantic;

  val jtag = Module(new alt_jtag_atlantic())

  val reg = Reg(next = jtag.io.t_dat)
  io.result := reg
}

object AlteraJtag {
  def main(args: Array[String]): Unit = {
    chiselMain(Array[String]("--backend", "v", "--targetDir", "generated"),
      () => Module(new AlteraJtag()))
  }
}
