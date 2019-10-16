package fifo

import chisel3._
import chisel3.util._

/**
  * FIFO combining the memory FIFO with a single stage double buffer FIFO
  * to decouple the combinational path from the memory read port to the output.
  */
class CombFifo[T <: Data](gen: T, depth: Int) extends Fifo(gen: T, depth: Int) {

  val memFifo = Module(new MemFifo(gen, depth))
  val bufferFIFO = Module(new DoubleBufferFifo(gen, 2))
  io.enq <> memFifo.io.enq
  memFifo.io.deq <> bufferFIFO.io.enq
  bufferFIFO.io.deq <> io.deq
}
