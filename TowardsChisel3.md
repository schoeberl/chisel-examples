# Towards Chisel 3

I will take notes along the way when moving this example collection
to Chisel 3.

See also [Chisel2 vs. Chisel3](https://github.com/freechipsproject/chisel3/wiki/Chisel3-vs-Chisel2)
and [prepare for the switch](https://github.com/ucb-bar/chisel2-deprecated#chisel3)

Not all steps will be needed for all projects. And maybe your project is
more advanced and needs additional steps.

## Preparation Work

 * Move to the latest version of Chisel2 (2.2.38) and check that everything works as expected
 * Mandatory changes:
   * Use ```Wire()```
   * Don't forget Wire on Vec of combinational logic
   * Vec(seq) does not work, use VecInit(seq)
 * Start to use some actual features, such as:
   * Input/Output is already available, but not UInt(8.W), so use UInt(width = 8) for now
   * nicer constants are available, such as 0.U
 * Use RegInit and RegNext
 * Install verilator, if not yet done

## The Switch

### Start with the Hello World

We want to get the LED blinking on an FPGA board.

 * Jump in and change `build.sbt` with a few more lines

```
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.0-SNAPSHOT"
```

or better:
```
libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.1.2"
```

 * first surprise: no more `chiselMain()`
 * Found the way to generate Verilog in the FAQ
```
object HelloWorld extends App {
  chisel3.Driver.execute(args, () => new HelloWorld)
}
```
 * To continue with the compatibility package `Chisel` I imported only the driver
```
import chisel3.Driver
```
 * Now generating hardware. Next surprise when trying to synthesize: the
name of the clock net was changed from `clk` to `clock`. No big deal.
 * Synthesize, configure the FPGA, and the LED blinks in Chisel 3
 * Change to chisel3 package


## Testing

Chisel testing has been moved to its own library and needs to be included
in the build.sbt.

```
libraryDependencies += "edu.berkeley.cs" %% "chisel-iotesters" % "1.2.2"
```
and imported and used as follows:

```
import chisel3.iotesters.PeekPokeTester

class LerosTester(dut: Leros) extends PeekPokeTester(dut) {
...

object LerosTester extends App {
  iotesters.Driver.execute(Array("--target-dir", "generated", "--fint-write-vcd"), () => new Leros(32, 10, args(0))) {
    c => new LerosTester(c)
  }
}
```

When using the compatibility layer, the ```iotester``` needs to be
taken from ```chisel3.iotester```:

```
chisel3.iotesters.Driver.execute(Array("--target-dir", "generated")...
```

The DUT shall not be wrapped into a Module, so change:
```
() => Module(new Play(4))
```
to
```
() => new Play(4)
```


## Compatibility Issues

Use latest Chisel 2 version (2.2.38)

according to Jim following should be kept in Chisel 2 and works with the Chisel 3 compatibility layer:

```
val dout = UInt(OUTPUT, 32)
```

then the change can be made on a file by file base.

Input and Output are added the Chisel 3.1.2 compatibility package,
so one can move to Input/Output/IO in Chisel 2 and switch.

Wires need to be wrapped into a ```Wire()```, even in the compatibility
mode. But this can also be done in 2.2.38.

## Compatibility Error

Following expression generates different values in Chisel 2 and Chisel 3:

```
Cat(1.U, Bits("b00"))
```

It is 4 in Chisel 2 and 2 in Chisel 3. Solution is to have the width explicitly,
e.g., ```0.U(2.W)```.

## Further Changes for Chisel 3 (Compatibility Issues)

One issues in the examples:

```
Input(gen.clone)
```
Solution: use ```cloneType```

Bits is now a second class type, therefore it breaks some Chisel 2 code, e.g.,:

```
    val byteArray = source.map(_.toByte).toArray
    source.close()
    val arr = new Array[Bits](byteArray.length)
    for (i <- 0 until byteArray.length) {
      arr(i) = Bits(byteArray(i), 8)
    }
    val rom = Vec[Bits](arr)
```

Suggestion is: use UInt.

This was legal Chisel 2 code, but broke in Chisel 3. It should be legal Chisel code:

```
  val shiftReg = Reg(init = UInt(0, 8))

  shiftReg(0) := inVal

  for (i <- 1 until 8) {
    shiftReg(i) := shiftReg(i - 1)
  }
```

Probably we can drop most clone() methods in the OCP code on Patmos?
Need to check how this works now and what e.g., a Vec uses (clone() or cloneType())?

```switch``` needs an import of ```import chisel3.util._```

```Enum(UInt(), 2)``` becomes ```Enum(2)``` and needs to import ```util``` as well.

```Cat``` is in ```utils``` as well. What should be used instead?

```Bundle``` is no abstract and following will fail:

```
class Base extends Module { val io = new Bundle() }
```

`println` has changed as well and expects a String now. Following is broken:

```
    println(i)
    println(peek(c.io.out))
```
Is ```toString``` the solution? Or are there other print functions now out?

Change `getWidth()` to `getWidth`

### ChiselError is gone

 * Change `ChiselError.error("error msg")` to `throw new Error("error msg")`
 * Change `ChiselError.info("info msg")` to `println("info msg")`

### No subword assignments

Subword assignments is gone in Chisel 3.
```
      when(rdData(7) === Bits(0)) {
        rdAddr(8, 4) := UInt(0x10)
        rdAddr(3, 0) := rdData
```

Rewrite the code, see also 
 * https://github.com/freechipsproject/chisel3/issues/244
 * https://github.com/freechipsproject/chisel3/issues/878

## BlackBox

BlackBox have no compatibility mode and need be changed on the switch,
see also https://github.com/freechipsproject/chisel3/issues/951


## Notes

Bulk connections work different in Chisel 3 than in Chisel 2 (and the compatibility layer).
