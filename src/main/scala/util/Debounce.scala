package util

import chisel3._
import chisel3.util._

class Debounce(fac: Int = 100000000/100) extends Module {
  val io = IO(new Bundle {
    val btnU = Input(Bool())
    val sw = Input(UInt(8.W))
    val led = Output(UInt(8.W))
  })

  def sync(v: Bool) = RegNext(RegNext(v))

  def rising(v: Bool) = v & !RegNext(v)

  def tickGen(fac: Int) = {
    val reg = RegInit(0.U(log2Up(fac).W))
    val tick = reg === (fac-1).U
    reg := Mux(tick, 0.U, reg + 1.U)
    tick
  }

  def filter(v: Bool, t: Bool) = {
    val reg = RegInit(0.U(3.W))
    when (t) {
      reg := Cat(reg(1, 0), v)
    }
    (reg(2) & reg(1)) | (reg(2) & reg(0)) | (reg(1) & reg(0))
  }

  val btnSync = sync(io.btnU)

  val tick = tickGen(fac)
  val btnDeb = Reg(Bool())
  when (tick) {
    btnDeb := btnSync
  }

  val btnFilter = filter(btnDeb, tick)
  val risingEdge = rising(btnFilter)

  // Use the rising edge of the debounced button
  // to count up
  val r1 = RegInit(0.U(8.W))
  when (risingEdge) {
    r1 := r1 + 1.U
  }

  io.led := r1
}

object Debounce extends App {
  println("Generating the Debounce hardware")
  (new chisel3.stage.ChiselStage).emitVerilog(new Debounce(), Array("--target-dir", "generated"))
}