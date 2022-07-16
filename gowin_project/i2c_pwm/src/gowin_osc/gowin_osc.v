//Copyright (C)2014-2022 Gowin Semiconductor Corporation.
//All rights reserved.
//File Title: IP file
//GOWIN Version: V1.9.8.06-1
//Part Number: GW1N-LV1QN32C6/I5
//Device: GW1N-1
//Created Time: Sat Jul 16 16:23:51 2022

module Gowin_OSC (oscout);

output oscout;

OSCH osc_inst (
    .OSCOUT(oscout)
);

defparam osc_inst.FREQ_DIV = 6;

endmodule //Gowin_OSC
