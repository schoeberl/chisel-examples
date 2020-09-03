import chisel3.iotesters.PeekPokeTester
import org.scalatest._

class HelloSpec extends FlatSpec with Matchers {

  "Hello" should "pass" in {
    chisel3.iotesters.Driver(() => new Hello()) { c =>
      new PeekPokeTester(c) {

        var ledStatus = -1
        println("Start the blinking LED")
        for (i <- 0 until 100) {
          step(10000)
          val ledNow = peek(c.io.led).toInt
          val s = if (ledNow == 0) "o" else "*"
          if (ledStatus != ledNow) {
            System.out.println(s)
            ledStatus = ledNow
          }
        }
        println("\nEnd the blinking LED")
      }
    } should be (true)
  }

}

