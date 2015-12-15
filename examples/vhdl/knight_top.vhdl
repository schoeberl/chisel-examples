--
-- Copyright: 2015, Technical University of Denmark, DTU Compute
-- Author: Martin Schoeberl (martin@jopdesign.com)
-- License: Simplified BSD License
--

--
-- Shallow VHDL top level for an FSM example.
--
-- Includes some 'magic' VHDL code to generate a reset after FPGA configuration.
--

library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity knight_top is
    port(
        clk : in  std_logic;
        io_led : out std_logic_vector(5 downto 0)
    );
end entity knight_top;

architecture rtl of knight_top is
    component KnightRider is
        port(
            clk    : in  std_logic;
            reset  : in  std_logic;
            io_led : out std_logic_vector(5 downto 0)
        );
    end component;


    -- for generation of internal reset
    signal int_res : std_logic;
    signal res_cnt : unsigned(2 downto 0) := "000"; -- for the simulation

    attribute altera_attribute : string;
    attribute altera_attribute of res_cnt : signal is "POWER_UP_LEVEL=LOW";

begin

    --
    --    internal reset generation
    --
    process(clk)
    begin
        if rising_edge(clk) then
            if (res_cnt /= "111") then
                res_cnt <= res_cnt + 1;
            end if;
            int_res <= not res_cnt(0) or not res_cnt(1) or not res_cnt(2);
        end if;
    end process;

    comp: KnightRider port map(
            clk, int_res, io_led
        );

end architecture rtl;
