# Towards Chisel 3

I will take notes along the way when moving this example collection
to Chisel 3.

See also [Chisel2 vs. Chisel3](https://github.com/freechipsproject/chisel3/wiki/Chisel3-vs-Chisel2).

Not all steps will be needed for all projects. And maybe your project is
more advanced and needs additional steps.

## Preparation Work

 * Move to the latest version of Chisel2 (2.2.38) and check that everything works as expected
 * Start to use some actual features, such as:
   * Input/Output is already available, but not UInt(8.W), so use UInt(width = 8) for now
   * nicer constants are available, such as 0.U
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
 * No generating hardware. Next surprise when trying to synthesize: the
name of the clock net was changed from `clk` to `clock`. No big deal.
 * Synthesize, configure the FPGA, and the LED blinks in Chisel 3

To be continued with:
 * Change to chisel3 package
 * The other examples, which also include testers

