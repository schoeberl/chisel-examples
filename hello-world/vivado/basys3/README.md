Vivado HOWTO:

1) make
2) Open Vivado and select Tools -> Run tcl script. The project will now be created in the home folder.
3) Generate bitsream and configure the FPGA with Vivado.  

Note: basys3 uses 100MHz clock, so the example led will flash at 0.5Hz instead. 
To change that without a PLL, CNT_MAX in chisel must be changed from 50000000 to 100000000
