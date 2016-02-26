--
-- Tops level for the UART
--

library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity uart_top is

port (
    clk : in std_logic;
--    reset : in std_logic;
    rxd : in std_logic;
    txd : out std_logic
);
end uart_top;

architecture rtl of uart_top is

component Sender is
port (clk : std_logic;
      reset : in std_logic;
--      io_rxd : in std_logic;
      io_txd : out std_logic);
end component;

   signal reset : std_logic;

begin
    -- TODO reset generation
    reset <= '0';
    
    s: Sender port map(clk, reset, txd);

--    txd <= rxd;

end rtl;
