package fifo

import chisel3._
import chisel3.util._

class BubbleFifo[T <: Data](gen: T, depth: Int) extends Fifo(gen: T, depth: Int) {

  private class Buffer[T <: Data](gen: T) extends Module {
    val io = IO(new FifoIO(gen))

    val empty :: full :: Nil = Enum(2)
    val stateReg = RegInit(empty)
    val dataReg = Reg(gen)

    switch(stateReg) {
      is (empty) {
        when (io.enq.valid) {
          stateReg := full
          dataReg := io.enq.bits
        }
      }
      is (full) {
        when (io.deq.ready) {
          stateReg := empty
        }
      }
    }

    io.enq.ready := (stateReg === empty)
    io.deq.valid := (stateReg === full)
    io.deq.bits := dataReg
  }

  private val buffers = Array.fill(depth) { Module(new Buffer(gen)) }
  for (i <- 0 until depth - 1) {
    buffers(i + 1).io.enq <> buffers(i).io.deq
  }

  io.enq <> buffers(0).io.enq
  io.deq <> buffers(depth - 1).io.deq
}
