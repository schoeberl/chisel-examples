--
-- Tops level for the UART
--

library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity uart_top is

port (
    clock : in std_logic;
--    reset : in std_logic;
-- led : out std_logic;
    rxd : in std_logic;
    txd : out std_logic
);
end uart_top;

architecture rtl of uart_top is

component UartMain is
port (clock : std_logic;
      reset : in std_logic;
      io_rxd : in std_logic;
      io_txd : out std_logic);
end component;

   signal reset : std_logic;
   signal int_res            : std_logic;
   signal res_reg1, res_reg2 : std_logic;
   signal res_cnt            : unsigned(2 downto 0) := "000"; -- for the simulation

   attribute altera_attribute : string;
   attribute altera_attribute of res_cnt : signal is "POWER_UP_LEVEL=LOW";


begin
   --
   --      internal reset generation
   --
   process(clock)
   begin
      if rising_edge(clock) then
         if (res_cnt /= "111") then
            res_cnt <= res_cnt + 1;
         end if;
         res_reg1 <= not res_cnt(0) or not res_cnt(1) or not res_cnt(2);
         res_reg2 <= res_reg1;
         int_res  <= res_reg2;
      end if;
   end process;    

   reset <= int_res;

    u: UartMain port map(clock, reset, rxd, txd);

--    led <= not rxd;
--    txd <= rxd;

end rtl;
