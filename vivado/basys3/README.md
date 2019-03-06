Vivado uart HOWTO:

1) Basys has 100MHz clock, so in order for uart to work the frequency value must be changed to 100,000,000 in UartMain constructor
2) Generate the verilog file
3) Open Vivado and select Tools -> Run tcl script. The project will now be created in the home folder.
4) Generate bitsream and configure the FPGA with Vivado  