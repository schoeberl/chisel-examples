import Chisel._

class Hz extends Module {
  val io = new Bundle {
    val input = Bool(INPUT)
    val output = Bool(OUTPUT)
  }
  val reg = Reg(init = Bool(false))
  when(io.input) {
    reg := Bool(true)
  }
  io.output := reg
}

class Top extends Module {
  val io = new Bundle {
    val input = Bool(INPUT)
    val output = Bool(OUTPUT)
  }
  val hz = Module(new Hz)
  hz.io <> io
}

class TopTests(c: Top) extends Tester(c) {
  step(1)
  poke(c.io.input, true)
  step(5)
}

object Top {
  def main(args: Array[String]): Unit = {
    chiselMainTest(args, () => Module(new Top())) {
      f => new TopTests(f)
    }
    //                chiselMain(args.slice(1, args.length), () => Module(new Top()), (c: Top) => new TopTests(c))
  }
}