

module TopWrap(
  output              pwm_ch_out_ch1,
  output              pwm_ch_out_ch2,
  output              pwm_ch_out_ch3,
  output              pwm_ch_out_ch4,
  output              pwm_ch_out_ch5,
  output              pwm_ch_out_ch6,
  output              pwm_ch_out_ch7,
  output              pwm_ch_out_ch8,
   inout              i2c_scl,
   inout              i2c_sda,
  input               resetn
);

wire clk_osc;

MyTopLevel mytop(
.pwm_ch_out_ch1(pwm_ch_out_ch1),
.pwm_ch_out_ch2(pwm_ch_out_ch2),
.pwm_ch_out_ch3(pwm_ch_out_ch3),
.pwm_ch_out_ch4(pwm_ch_out_ch4),
.pwm_ch_out_ch5(pwm_ch_out_ch5),
.pwm_ch_out_ch6(pwm_ch_out_ch6),
.pwm_ch_out_ch7(pwm_ch_out_ch7),
.pwm_ch_out_ch8(pwm_ch_out_ch8),
.i2c_scl(i2c_scl),
.i2c_sda(i2c_sda),
.clk(clk_osc),
.resetn(resetn)
);

Gowin_OSC osc(clk_osc);


endmodule

