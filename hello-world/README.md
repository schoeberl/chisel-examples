# Hardware Hello World

Author: Martin Schoeberl (martin@jopdesign.com)

This is a minimum build environment to start with Chisel.
The example is a blinking LED in an FPGA - The hardware version of "Hello World"


The example project consists of:

 * src/main/scala/Hello.scala The source of the hardware description of a blinking LED  
 * Makefile               drives the build process 
 * build.sbt              Configuration to download Chisel and for the Chisel project 
 * quartus/altde2-115     Altera Quartus project files for the DE2-115 board


For some configurations a top-level is used to connect reset to 0

 * verilog/hello_top.v    is a top level file for the FPGA

Additional boards

 * quartus/altde0         Altera Quartus project files for the DE0 board 
 * quartus/altde1         Altera Quartus project files for the DE1 board 
 * quartus/bemicro        Altera Quartus project files for the BeMicro board 
 * quartus/bemicro_cv_a9  Altera Quartus project files for the new BeMicro board

Remarks and comments are distributed over the individual files.

HOWTO:

 * make (or sbt run)
 * Open the project file in Quartus
   * Compile with the play button
   * Configure the FPGA with the programmer
   
If it happens that you do not have access to an FPGA board, you can run the
blinking LED in simulation. To avoid to simulate for 100000000 clock cycles
change the factor in ```Hello.scala``` on following line from:

```
  val CNT_MAX = (100000000 / 2 - 1).U;
```
to
```
  val CNT_MAX = (50000 / 2 - 1).U;
```
and run the simulation with
```
sbt test
```
You should see in the terminal a *simulation* of the blinking LED.
