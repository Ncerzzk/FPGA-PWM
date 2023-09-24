//Copyright (C)2014-2022 Gowin Semiconductor Corporation.
//All rights reserved.
//File Title: IP file
//GOWIN Version: V1.9.8.06-1
//Part Number: GW1N-LV1QN32C5/I4
//Device: GW1N-1
//Created Time: Sat Sep 23 23:46:26 2023

module Gowin_OSC (oscout);

output oscout;

OSCH osc_inst (
    .OSCOUT(oscout)
);

defparam osc_inst.FREQ_DIV = 4;

endmodule //Gowin_OSC
