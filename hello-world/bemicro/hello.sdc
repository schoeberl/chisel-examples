###########################################################################
# SDC files for Cycore BeMicro
###########################################################################
 
 
# Clock in input pin (16 MHz)
create_clock -period 62.5 [get_ports clk]

derive_clock_uncertainty
# Create generated clocks based on PLLs
derive_pll_clocks

# ** Input/Output Delays
#    -------------------
# MS: I want following constrains for the SRAM connection
# Input:
#    maximum Tsu 2.2 ns
#    maximum Tho ? ns (could be 0-2, will be negative anyway)
# Output:
#    maximum Tco 3 ns
#
# Are the following constraints correct?
# I'm confused by the min/max input delay notion.

# Use FPGA-centric constraints (general pins)
# Tsu 5 ns
set_max_delay -from [all_inputs] -to [all_registers] 5
# the value is -th, so a negative hold time would be positive
set_min_delay -from [all_inputs] -to [all_registers] -0.0
# Tco 10 ns
set_max_delay -from [all_registers] -to [all_outputs] 10
# WTF shall this constraint be? A hold time on an output port?
set_min_delay -from [all_registers] -to [all_outputs] -0.0

# Use FPGA-centric constraints (SRAM pins)
# Tsu 2 ns
# set_max_delay -from [get_ports *] -to [get_registers {*ram*}] 2.2
# Tco 3 ns
# set_max_delay -from [get_registers *] -to [get_ports {ram*}] 3

