module Gowin_OSC (oscout);

output oscout;

OSCH osc_inst (
    .OSCOUT(oscout)
);

defparam osc_inst.FREQ_DIV = 2;

endmodule //Gowin_OSC

