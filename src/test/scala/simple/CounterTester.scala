/*
 * A simple counter example with configurable bit width and with a test bench.
 * 
 */

package simple

import chisel3._
import chisel3.iotesters.PeekPokeTester

/**
 * Test the counter by printing out the value at each clock cycle.
 */
class CounterTester(c: Counter) extends PeekPokeTester(c) {

  for (i <- 0 until 5) {
    println(i.toString + ": " + peek(c.io.out).toString())
    step(1)
  }
}

/**
 * Create a counter and a tester.
 */
object CounterTester extends App {

  iotesters.Driver.execute(Array[String](), () => new Counter(2)) {
    c => new CounterTester(c)
  }
}
