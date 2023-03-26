// Generator : SpinalHDL v1.7.0    git head : eca519e78d4e6022e34911ec300a432ed9db8220
// Component : SPI_PWM_Top
// Git hash  : 59e80837b87222d6971a7a38fbe620402950876f

`timescale 1ns/1ps

module SPI_PWM_Top (
  input               spi_pins_sclk,
  input               spi_pins_mosi,
  output              spi_pins_miso_write,
  output              spi_pins_miso_writeEnable,
  input               spi_pins_ss,
  output              pwm_out_ch0,
  output              pwm_out_ch1,
  output              pwm_out_ch2,
  output              pwm_out_ch3,
  output              pwm_out_ch4,
  output              pwm_out_ch5,
  output              pwm_out_ch6,
  output              pwm_out_ch7,
  input               reset
);

  wire                osc_oscout;
  wire       [7:0]    spi_pwm_1_apb_m_PADDR;
  wire       [0:0]    spi_pwm_1_apb_m_PSEL;
  wire                spi_pwm_1_apb_m_PENABLE;
  wire                spi_pwm_1_apb_m_PWRITE;
  wire       [31:0]   spi_pwm_1_apb_m_PWDATA;
  wire                spi_pwm_1_pwm_pwm_out_ch0;
  wire                spi_pwm_1_pwm_pwm_out_ch1;
  wire                spi_pwm_1_pwm_pwm_out_ch2;
  wire                spi_pwm_1_pwm_pwm_out_ch3;
  wire                spi_pwm_1_pwm_pwm_out_ch4;
  wire                spi_pwm_1_pwm_pwm_out_ch5;
  wire                spi_pwm_1_pwm_pwm_out_ch6;
  wire                spi_pwm_1_pwm_pwm_out_ch7;
  wire                spi_slave_ctrl_io_apb_PREADY;
  wire       [31:0]   spi_slave_ctrl_io_apb_PRDATA;
  wire                spi_slave_ctrl_io_spi_miso_write;
  wire                spi_slave_ctrl_io_spi_miso_writeEnable;
  wire                spi_slave_ctrl_io_interrupt;

  Gowin_OSC osc (
    .oscout (osc_oscout)  //o
  );
  SPI_PWM spi_pwm_1 (
    .apb_m_PADDR     (spi_pwm_1_apb_m_PADDR[7:0]        ), //o
    .apb_m_PSEL      (spi_pwm_1_apb_m_PSEL              ), //o
    .apb_m_PENABLE   (spi_pwm_1_apb_m_PENABLE           ), //o
    .apb_m_PREADY    (spi_slave_ctrl_io_apb_PREADY      ), //i
    .apb_m_PWRITE    (spi_pwm_1_apb_m_PWRITE            ), //o
    .apb_m_PWDATA    (spi_pwm_1_apb_m_PWDATA[31:0]      ), //o
    .apb_m_PRDATA    (spi_slave_ctrl_io_apb_PRDATA[31:0]), //i
    .interrupt       (spi_slave_ctrl_io_interrupt       ), //i
    .sclk            (spi_pins_sclk                     ), //i
    .mosi            (spi_pins_mosi                     ), //i
    .ss              (spi_pins_ss                       ), //i
    .pwm_pwm_out_ch0 (spi_pwm_1_pwm_pwm_out_ch0         ), //o
    .pwm_pwm_out_ch1 (spi_pwm_1_pwm_pwm_out_ch1         ), //o
    .pwm_pwm_out_ch2 (spi_pwm_1_pwm_pwm_out_ch2         ), //o
    .pwm_pwm_out_ch3 (spi_pwm_1_pwm_pwm_out_ch3         ), //o
    .pwm_pwm_out_ch4 (spi_pwm_1_pwm_pwm_out_ch4         ), //o
    .pwm_pwm_out_ch5 (spi_pwm_1_pwm_pwm_out_ch5         ), //o
    .pwm_pwm_out_ch6 (spi_pwm_1_pwm_pwm_out_ch6         ), //o
    .pwm_pwm_out_ch7 (spi_pwm_1_pwm_pwm_out_ch7         ), //o
    .oscout          (osc_oscout                        ), //i
    .reset           (reset                             )  //i
  );
  Apb3SpiSlaveCtrl spi_slave_ctrl (
    .io_apb_PADDR            (spi_pwm_1_apb_m_PADDR[7:0]            ), //i
    .io_apb_PSEL             (spi_pwm_1_apb_m_PSEL                  ), //i
    .io_apb_PENABLE          (spi_pwm_1_apb_m_PENABLE               ), //i
    .io_apb_PREADY           (spi_slave_ctrl_io_apb_PREADY          ), //o
    .io_apb_PWRITE           (spi_pwm_1_apb_m_PWRITE                ), //i
    .io_apb_PWDATA           (spi_pwm_1_apb_m_PWDATA[31:0]          ), //i
    .io_apb_PRDATA           (spi_slave_ctrl_io_apb_PRDATA[31:0]    ), //o
    .io_spi_sclk             (spi_pins_sclk                         ), //i
    .io_spi_mosi             (spi_pins_mosi                         ), //i
    .io_spi_miso_write       (spi_slave_ctrl_io_spi_miso_write      ), //o
    .io_spi_miso_writeEnable (spi_slave_ctrl_io_spi_miso_writeEnable), //o
    .io_spi_ss               (spi_pins_ss                           ), //i
    .io_interrupt            (spi_slave_ctrl_io_interrupt           ), //o
    .oscout                  (osc_oscout                            ), //i
    .reset                   (reset                                 )  //i
  );
  assign pwm_out_ch0 = spi_pwm_1_pwm_pwm_out_ch0;
  assign pwm_out_ch1 = spi_pwm_1_pwm_pwm_out_ch1;
  assign pwm_out_ch2 = spi_pwm_1_pwm_pwm_out_ch2;
  assign pwm_out_ch3 = spi_pwm_1_pwm_pwm_out_ch3;
  assign pwm_out_ch4 = spi_pwm_1_pwm_pwm_out_ch4;
  assign pwm_out_ch5 = spi_pwm_1_pwm_pwm_out_ch5;
  assign pwm_out_ch6 = spi_pwm_1_pwm_pwm_out_ch6;
  assign pwm_out_ch7 = spi_pwm_1_pwm_pwm_out_ch7;
  assign spi_pins_miso_write = spi_slave_ctrl_io_spi_miso_write;
  assign spi_pins_miso_writeEnable = spi_slave_ctrl_io_spi_miso_writeEnable;

endmodule

module Apb3SpiSlaveCtrl (
  input      [7:0]    io_apb_PADDR,
  input      [0:0]    io_apb_PSEL,
  input               io_apb_PENABLE,
  output              io_apb_PREADY,
  input               io_apb_PWRITE,
  input      [31:0]   io_apb_PWDATA,
  output reg [31:0]   io_apb_PRDATA,
  input               io_spi_sclk,
  input               io_spi_mosi,
  output              io_spi_miso_write,
  output              io_spi_miso_writeEnable,
  input               io_spi_ss,
  output              io_interrupt,
  input               oscout,
  input               reset
);

  reg                 spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_ready;
  wire                spiCtrl_io_rx_valid;
  wire       [7:0]    spiCtrl_io_rx_payload;
  wire                spiCtrl_io_tx_ready;
  wire                spiCtrl_io_txError;
  wire                spiCtrl_io_ssFilted;
  wire                spiCtrl_io_spi_miso_write;
  wire                spiCtrl_io_spi_miso_writeEnable;
  wire                bridge_txLogic_streamUnbuffered_queueWithAvailability_io_push_ready;
  wire                bridge_txLogic_streamUnbuffered_queueWithAvailability_io_pop_valid;
  wire       [7:0]    bridge_txLogic_streamUnbuffered_queueWithAvailability_io_pop_payload;
  wire       [1:0]    bridge_txLogic_streamUnbuffered_queueWithAvailability_io_occupancy;
  wire       [1:0]    bridge_txLogic_streamUnbuffered_queueWithAvailability_io_availability;
  wire                spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_push_ready;
  wire                spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_valid;
  wire       [7:0]    spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_payload;
  wire       [1:0]    spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_occupancy;
  wire       [1:0]    spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_availability;
  wire       [0:0]    _zz_bridge_interruptCtrl_ssEnabledInt;
  wire       [0:0]    _zz_bridge_interruptCtrl_ssDisabledInt;
  wire                busCtrl_askWrite;
  wire                busCtrl_askRead;
  wire                busCtrl_doWrite;
  wire                busCtrl_doRead;
  reg                 _zz_bridge_txLogic_streamUnbuffered_valid;
  wire                bridge_txLogic_streamUnbuffered_valid;
  wire                bridge_txLogic_streamUnbuffered_ready;
  wire       [7:0]    bridge_txLogic_streamUnbuffered_payload;
  reg                 bridge_rxLogic_listen;
  wire                spiCtrl_io_rx_takeWhen_valid;
  wire       [7:0]    spiCtrl_io_rx_takeWhen_payload;
  wire                spiCtrl_io_rx_takeWhen_toStream_valid;
  wire                spiCtrl_io_rx_takeWhen_toStream_ready;
  wire       [7:0]    spiCtrl_io_rx_takeWhen_toStream_payload;
  reg                 bridge_interruptCtrl_txIntEnable;
  reg                 bridge_interruptCtrl_rxIntEnable;
  reg                 bridge_interruptCtrl_ssEnabledIntEnable;
  reg                 bridge_interruptCtrl_ssDisabledIntEnable;
  wire                bridge_interruptCtrl_ssFiltedEdges_rise;
  wire                bridge_interruptCtrl_ssFiltedEdges_fall;
  wire                bridge_interruptCtrl_ssFiltedEdges_toggle;
  reg                 spiCtrl_io_ssFilted_regNext;
  wire                bridge_interruptCtrl_txInt;
  wire                bridge_interruptCtrl_rxInt;
  reg                 bridge_interruptCtrl_ssEnabledInt;
  wire                when_SpiSlaveCtrl_l97;
  reg                 bridge_interruptCtrl_ssDisabledInt;
  wire                when_SpiSlaveCtrl_l98;
  reg                 when_BusSlaveFactory_l335;
  wire                when_BusSlaveFactory_l341;
  reg                 when_BusSlaveFactory_l335_1;
  wire                when_BusSlaveFactory_l341_1;
  wire                bridge_interruptCtrl_interrupt;
  reg                 _zz_io_kind_cpol;
  reg                 _zz_io_kind_cpha;
  wire       [1:0]    _zz_io_kind_cpol_1;

  assign _zz_bridge_interruptCtrl_ssEnabledInt = 1'b0;
  assign _zz_bridge_interruptCtrl_ssDisabledInt = 1'b0;
  SpiSlaveCtrl spiCtrl (
    .io_kind_cpol            (_zz_io_kind_cpol                                                         ), //i
    .io_kind_cpha            (_zz_io_kind_cpha                                                         ), //i
    .io_rx_valid             (spiCtrl_io_rx_valid                                                      ), //o
    .io_rx_payload           (spiCtrl_io_rx_payload[7:0]                                               ), //o
    .io_tx_valid             (bridge_txLogic_streamUnbuffered_queueWithAvailability_io_pop_valid       ), //i
    .io_tx_ready             (spiCtrl_io_tx_ready                                                      ), //o
    .io_tx_payload           (bridge_txLogic_streamUnbuffered_queueWithAvailability_io_pop_payload[7:0]), //i
    .io_txError              (spiCtrl_io_txError                                                       ), //o
    .io_ssFilted             (spiCtrl_io_ssFilted                                                      ), //o
    .io_spi_sclk             (io_spi_sclk                                                              ), //i
    .io_spi_mosi             (io_spi_mosi                                                              ), //i
    .io_spi_miso_write       (spiCtrl_io_spi_miso_write                                                ), //o
    .io_spi_miso_writeEnable (spiCtrl_io_spi_miso_writeEnable                                          ), //o
    .io_spi_ss               (io_spi_ss                                                                ), //i
    .oscout                  (oscout                                                                   ), //i
    .reset                   (reset                                                                    )  //i
  );
  StreamFifo bridge_txLogic_streamUnbuffered_queueWithAvailability (
    .io_push_valid   (bridge_txLogic_streamUnbuffered_valid                                     ), //i
    .io_push_ready   (bridge_txLogic_streamUnbuffered_queueWithAvailability_io_push_ready       ), //o
    .io_push_payload (bridge_txLogic_streamUnbuffered_payload[7:0]                              ), //i
    .io_pop_valid    (bridge_txLogic_streamUnbuffered_queueWithAvailability_io_pop_valid        ), //o
    .io_pop_ready    (spiCtrl_io_tx_ready                                                       ), //i
    .io_pop_payload  (bridge_txLogic_streamUnbuffered_queueWithAvailability_io_pop_payload[7:0] ), //o
    .io_flush        (1'b0                                                                      ), //i
    .io_occupancy    (bridge_txLogic_streamUnbuffered_queueWithAvailability_io_occupancy[1:0]   ), //o
    .io_availability (bridge_txLogic_streamUnbuffered_queueWithAvailability_io_availability[1:0]), //o
    .oscout          (oscout                                                                    ), //i
    .reset           (reset                                                                     )  //i
  );
  StreamFifo spiCtrl_io_rx_takeWhen_queueWithOccupancy (
    .io_push_valid   (spiCtrl_io_rx_takeWhen_toStream_valid                         ), //i
    .io_push_ready   (spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_push_ready       ), //o
    .io_push_payload (spiCtrl_io_rx_takeWhen_toStream_payload[7:0]                  ), //i
    .io_pop_valid    (spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_valid        ), //o
    .io_pop_ready    (spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_ready        ), //i
    .io_pop_payload  (spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_payload[7:0] ), //o
    .io_flush        (1'b0                                                          ), //i
    .io_occupancy    (spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_occupancy[1:0]   ), //o
    .io_availability (spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_availability[1:0]), //o
    .oscout          (oscout                                                        ), //i
    .reset           (reset                                                         )  //i
  );
  assign io_spi_miso_write = spiCtrl_io_spi_miso_write;
  assign io_spi_miso_writeEnable = spiCtrl_io_spi_miso_writeEnable;
  assign io_apb_PREADY = 1'b1;
  always @(*) begin
    io_apb_PRDATA = 32'h0;
    case(io_apb_PADDR)
      8'h0 : begin
        io_apb_PRDATA[31 : 31] = (spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_valid ^ 1'b0);
        io_apb_PRDATA[7 : 0] = spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_payload;
        io_apb_PRDATA[17 : 16] = spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_occupancy;
      end
      8'h04 : begin
        io_apb_PRDATA[17 : 16] = bridge_txLogic_streamUnbuffered_queueWithAvailability_io_availability;
        io_apb_PRDATA[15 : 15] = bridge_rxLogic_listen;
        io_apb_PRDATA[0 : 0] = bridge_interruptCtrl_txIntEnable;
        io_apb_PRDATA[1 : 1] = bridge_interruptCtrl_rxIntEnable;
        io_apb_PRDATA[2 : 2] = bridge_interruptCtrl_ssEnabledIntEnable;
        io_apb_PRDATA[3 : 3] = bridge_interruptCtrl_ssDisabledIntEnable;
        io_apb_PRDATA[8 : 8] = bridge_interruptCtrl_txInt;
        io_apb_PRDATA[9 : 9] = bridge_interruptCtrl_rxInt;
        io_apb_PRDATA[10 : 10] = bridge_interruptCtrl_ssEnabledInt;
        io_apb_PRDATA[11 : 11] = bridge_interruptCtrl_ssDisabledInt;
      end
      default : begin
      end
    endcase
  end

  assign busCtrl_askWrite = ((io_apb_PSEL[0] && io_apb_PENABLE) && io_apb_PWRITE);
  assign busCtrl_askRead = ((io_apb_PSEL[0] && io_apb_PENABLE) && (! io_apb_PWRITE));
  assign busCtrl_doWrite = (((io_apb_PSEL[0] && io_apb_PENABLE) && io_apb_PREADY) && io_apb_PWRITE);
  assign busCtrl_doRead = (((io_apb_PSEL[0] && io_apb_PENABLE) && io_apb_PREADY) && (! io_apb_PWRITE));
  always @(*) begin
    _zz_bridge_txLogic_streamUnbuffered_valid = 1'b0;
    case(io_apb_PADDR)
      8'h0 : begin
        if(busCtrl_doWrite) begin
          _zz_bridge_txLogic_streamUnbuffered_valid = 1'b1;
        end
      end
      default : begin
      end
    endcase
  end

  assign bridge_txLogic_streamUnbuffered_valid = _zz_bridge_txLogic_streamUnbuffered_valid;
  assign bridge_txLogic_streamUnbuffered_payload = io_apb_PWDATA[7 : 0];
  assign bridge_txLogic_streamUnbuffered_ready = bridge_txLogic_streamUnbuffered_queueWithAvailability_io_push_ready;
  assign spiCtrl_io_rx_takeWhen_valid = (spiCtrl_io_rx_valid && bridge_rxLogic_listen);
  assign spiCtrl_io_rx_takeWhen_payload = spiCtrl_io_rx_payload;
  assign spiCtrl_io_rx_takeWhen_toStream_valid = spiCtrl_io_rx_takeWhen_valid;
  assign spiCtrl_io_rx_takeWhen_toStream_payload = spiCtrl_io_rx_takeWhen_payload;
  assign spiCtrl_io_rx_takeWhen_toStream_ready = spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_push_ready;
  always @(*) begin
    spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_ready = 1'b0;
    case(io_apb_PADDR)
      8'h0 : begin
        if(busCtrl_doRead) begin
          spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_ready = 1'b1;
        end
      end
      default : begin
      end
    endcase
  end

  assign bridge_interruptCtrl_ssFiltedEdges_rise = ((! spiCtrl_io_ssFilted_regNext) && spiCtrl_io_ssFilted);
  assign bridge_interruptCtrl_ssFiltedEdges_fall = (spiCtrl_io_ssFilted_regNext && (! spiCtrl_io_ssFilted));
  assign bridge_interruptCtrl_ssFiltedEdges_toggle = (spiCtrl_io_ssFilted_regNext != spiCtrl_io_ssFilted);
  assign bridge_interruptCtrl_txInt = (bridge_interruptCtrl_txIntEnable && (! bridge_txLogic_streamUnbuffered_queueWithAvailability_io_pop_valid));
  assign bridge_interruptCtrl_rxInt = (bridge_interruptCtrl_rxIntEnable && spiCtrl_io_rx_takeWhen_queueWithOccupancy_io_pop_valid);
  assign when_SpiSlaveCtrl_l97 = (! bridge_interruptCtrl_ssEnabledIntEnable);
  assign when_SpiSlaveCtrl_l98 = (! bridge_interruptCtrl_ssDisabledIntEnable);
  always @(*) begin
    when_BusSlaveFactory_l335 = 1'b0;
    case(io_apb_PADDR)
      8'h04 : begin
        if(busCtrl_doWrite) begin
          when_BusSlaveFactory_l335 = 1'b1;
        end
      end
      default : begin
      end
    endcase
  end

  assign when_BusSlaveFactory_l341 = io_apb_PWDATA[12];
  always @(*) begin
    when_BusSlaveFactory_l335_1 = 1'b0;
    case(io_apb_PADDR)
      8'h04 : begin
        if(busCtrl_doWrite) begin
          when_BusSlaveFactory_l335_1 = 1'b1;
        end
      end
      default : begin
      end
    endcase
  end

  assign when_BusSlaveFactory_l341_1 = io_apb_PWDATA[13];
  assign bridge_interruptCtrl_interrupt = (((bridge_interruptCtrl_rxInt || bridge_interruptCtrl_txInt) || bridge_interruptCtrl_ssEnabledInt) || bridge_interruptCtrl_ssDisabledInt);
  assign io_interrupt = bridge_interruptCtrl_interrupt;
  assign _zz_io_kind_cpol_1 = io_apb_PWDATA[1 : 0];
  always @(posedge oscout or posedge reset) begin
    if(reset) begin
      bridge_rxLogic_listen <= 1'b0;
      bridge_interruptCtrl_txIntEnable <= 1'b0;
      bridge_interruptCtrl_rxIntEnable <= 1'b0;
      bridge_interruptCtrl_ssEnabledIntEnable <= 1'b0;
      bridge_interruptCtrl_ssDisabledIntEnable <= 1'b0;
      spiCtrl_io_ssFilted_regNext <= 1'b1;
      bridge_interruptCtrl_ssEnabledInt <= 1'b0;
      bridge_interruptCtrl_ssDisabledInt <= 1'b0;
    end else begin
      spiCtrl_io_ssFilted_regNext <= spiCtrl_io_ssFilted;
      if(bridge_interruptCtrl_ssFiltedEdges_fall) begin
        bridge_interruptCtrl_ssEnabledInt <= 1'b1;
      end
      if(when_SpiSlaveCtrl_l97) begin
        bridge_interruptCtrl_ssEnabledInt <= 1'b0;
      end
      if(bridge_interruptCtrl_ssFiltedEdges_rise) begin
        bridge_interruptCtrl_ssDisabledInt <= 1'b1;
      end
      if(when_SpiSlaveCtrl_l98) begin
        bridge_interruptCtrl_ssDisabledInt <= 1'b0;
      end
      if(when_BusSlaveFactory_l335) begin
        if(when_BusSlaveFactory_l341) begin
          bridge_interruptCtrl_ssEnabledInt <= _zz_bridge_interruptCtrl_ssEnabledInt[0];
        end
      end
      if(when_BusSlaveFactory_l335_1) begin
        if(when_BusSlaveFactory_l341_1) begin
          bridge_interruptCtrl_ssDisabledInt <= _zz_bridge_interruptCtrl_ssDisabledInt[0];
        end
      end
      case(io_apb_PADDR)
        8'h04 : begin
          if(busCtrl_doWrite) begin
            bridge_rxLogic_listen <= io_apb_PWDATA[15];
            bridge_interruptCtrl_txIntEnable <= io_apb_PWDATA[0];
            bridge_interruptCtrl_rxIntEnable <= io_apb_PWDATA[1];
            bridge_interruptCtrl_ssEnabledIntEnable <= io_apb_PWDATA[2];
            bridge_interruptCtrl_ssDisabledIntEnable <= io_apb_PWDATA[3];
          end
        end
        default : begin
        end
      endcase
    end
  end

  always @(posedge oscout) begin
    case(io_apb_PADDR)
      8'h08 : begin
        if(busCtrl_doWrite) begin
          _zz_io_kind_cpol <= _zz_io_kind_cpol_1[0];
          _zz_io_kind_cpha <= _zz_io_kind_cpol_1[1];
        end
      end
      default : begin
      end
    endcase
  end


endmodule

module SPI_PWM (
  output reg [7:0]    apb_m_PADDR,
  output reg [0:0]    apb_m_PSEL,
  output reg          apb_m_PENABLE,
  input               apb_m_PREADY,
  output reg          apb_m_PWRITE,
  output reg [31:0]   apb_m_PWDATA,
  input      [31:0]   apb_m_PRDATA,
  input               interrupt,
  input               sclk,
  input               mosi,
  input               ss,
  output              pwm_pwm_out_ch0,
  output              pwm_pwm_out_ch1,
  output              pwm_pwm_out_ch2,
  output              pwm_pwm_out_ch3,
  output              pwm_pwm_out_ch4,
  output              pwm_pwm_out_ch5,
  output              pwm_pwm_out_ch6,
  output              pwm_pwm_out_ch7,
  input               oscout,
  input               reset
);
  localparam APB3Phase_IDLE = 2'd0;
  localparam APB3Phase_SETUP = 2'd1;
  localparam APB3Phase_ACCESS_1 = 2'd2;
  localparam spi_fsm_being_written_fsm_enumDef_BOOT = 2'd0;
  localparam spi_fsm_being_written_fsm_enumDef_init = 2'd1;
  localparam spi_fsm_being_written_fsm_enumDef_wait_s = 2'd2;
  localparam spi_fsm_being_written_fsm_enumDef_read = 2'd3;
  localparam spi_fsm_enumDef_BOOT = 2'd0;
  localparam spi_fsm_enumDef_idle = 2'd1;
  localparam spi_fsm_enumDef_being_written = 2'd2;
  localparam spi_fsm_enumDef_start_transfer = 2'd3;

  wire       [15:0]   _zz_regs_data_port0;
  wire       [15:0]   _zz_regs_data_port1;
  wire       [15:0]   _zz_regs_data_port2;
  wire       [15:0]   _zz_regs_data_port3;
  wire       [15:0]   _zz_regs_data_port4;
  wire       [15:0]   _zz_regs_data_port5;
  wire       [15:0]   _zz_regs_data_port6;
  wire       [15:0]   _zz_regs_data_port7;
  wire       [15:0]   _zz_regs_data_port8;
  wire       [15:0]   _zz_regs_data_port9;
  wire       [15:0]   _zz_regs_data_port10;
  wire       [15:0]   _zz_regs_data_port11;
  wire       [15:0]   _zz_regs_data_port12;
  wire       [15:0]   _zz_regs_data_port13;
  wire       [15:0]   _zz_regs_data_port14;
  wire       [15:0]   _zz_regs_data_port16;
  wire                sclk_buffercc_io_dataOut;
  wire                mosi_buffercc_io_dataOut;
  wire                ss_buffercc_io_dataOut;
  wire       [3:0]    _zz_regs_data_port;
  wire       [3:0]    _zz_regs_data_port_1;
  wire       [3:0]    _zz_regs_data_port_2;
  wire       [3:0]    _zz_regs_data_port_3;
  wire       [3:0]    _zz_regs_data_port_4;
  wire       [3:0]    _zz_regs_data_port_5;
  wire       [3:0]    _zz_regs_data_port_6;
  wire       [3:0]    _zz_regs_data_port_7;
  wire       [3:0]    _zz_regs_data_port_8;
  wire       [3:0]    _zz_regs_data_port_9;
  wire       [3:0]    _zz__zz_switch_Misc_l211;
  wire       [3:0]    _zz_regs_data_port_10;
  wire       [3:0]    _zz_regs_data_port_11;
  wire       [3:0]    _zz_pwm_pwm_regs_config;
  wire       [3:0]    _zz_regs_data_port_12;
  wire       [3:0]    _zz_pwm_pwm_regs_watchdog;
  wire       [3:0]    _zz_regs_data_port_13;
  wire       [3:0]    _zz_pwm_pwm_regs_timeout_max_high;
  wire       [3:0]    _zz_regs_data_port_14;
  wire       [3:0]    _zz_pwm_pwm_regs_timeout_max_low;
  wire       [3:0]    _zz__zz_pwm_sub_pwms_0_period_buf;
  wire       [3:0]    _zz__zz_when_PWM_l17;
  wire       [3:0]    _zz_pwm_pwm_area_channels_0_output_1;
  wire       [3:0]    _zz_pwm_pwm_area_channels_1_output_1;
  wire       [3:0]    _zz_pwm_pwm_area_channels_2_output_1;
  wire       [3:0]    _zz_pwm_pwm_area_channels_3_output_1;
  wire       [3:0]    _zz_pwm_pwm_area_channels_4_output_1;
  wire       [3:0]    _zz_pwm_pwm_area_channels_5_output_1;
  wire       [3:0]    _zz_pwm_pwm_area_channels_6_output_1;
  wire       [3:0]    _zz_pwm_pwm_area_channels_7_output;
  wire       [2:0]    _zz_spi_fsm_sclk_count_valueNext;
  wire       [0:0]    _zz_spi_fsm_sclk_count_valueNext_1;
  wire       [8:0]    _zz_spi_fsm_temp_rx;
  wire       [15:0]   _zz_regs_data_port_15;
  wire       [7:0]    _zz_apb_m_PWDATA_2;
  wire       [7:0]    _zz_apb_m_PWDATA_3;
  reg        [3:0]    _zz_when_utils_l25;
  reg        [2:0]    _zz_when_utils_l25_1;
  reg                 _zz_1;
  wire                sclk_sync;
  wire                mosi_sync;
  wire                ss_sync;
  wire       [31:0]   spi_slave_regs_data;
  wire       [31:0]   spi_slave_regs_status;
  wire       [31:0]   spi_slave_regs_config;
  reg                 regs_outRange;
  wire       [0:0]    _zz_pwm_pwm_area_channels_0_output;
  wire       [1:0]    _zz_pwm_pwm_area_channels_1_output;
  wire       [1:0]    _zz_pwm_pwm_area_channels_2_output;
  wire       [2:0]    _zz_pwm_pwm_area_channels_3_output;
  wire       [2:0]    _zz_pwm_pwm_area_channels_4_output;
  wire       [2:0]    _zz_pwm_pwm_area_channels_5_output;
  wire       [2:0]    _zz_pwm_pwm_area_channels_6_output;
  wire       [15:0]   _zz_switch_Misc_l211;
  wire       [15:0]   pwm_pwm_regs_config;
  wire       [15:0]   pwm_pwm_regs_watchdog;
  wire       [15:0]   pwm_pwm_regs_timeout_max_high;
  wire       [15:0]   pwm_pwm_regs_timeout_max_low;
  wire                when_PWM_l19;
  wire       [15:0]   _zz_pwm_sub_pwms_0_period_buf;
  reg        [15:0]   pwm_sub_pwms_0_counter;
  reg        [15:0]   pwm_sub_pwms_0_period_buf;
  wire                when_PWM_l19_1;
  wire       [15:0]   _zz_when_PWM_l17;
  reg        [15:0]   _zz_pwm_pwm_area_channels_0_counter_map;
  reg        [15:0]   _zz_when_PWM_l17_1;
  reg        [4:0]    pwm_pre_divicder_counter;
  wire       [4:0]    pwm_pre_divicder_divider_val;
  reg                 pwm_pre_divicder_clk_en;
  wire                when_PWM_l93;
  wire                when_PWM_l96;
  wire       [1:0]    switch_Misc_l211;
  wire                pwm_pwm_area_channels_0_output;
  reg        [15:0]   pwm_pwm_area_channels_0_counter_map;
  wire       [1:0]    switch_Misc_l211_1;
  wire                pwm_pwm_area_channels_1_output;
  reg        [15:0]   pwm_pwm_area_channels_1_counter_map;
  wire       [1:0]    switch_Misc_l211_2;
  wire                pwm_pwm_area_channels_2_output;
  reg        [15:0]   pwm_pwm_area_channels_2_counter_map;
  wire       [1:0]    switch_Misc_l211_3;
  wire                pwm_pwm_area_channels_3_output;
  reg        [15:0]   pwm_pwm_area_channels_3_counter_map;
  wire       [1:0]    switch_Misc_l211_4;
  wire                pwm_pwm_area_channels_4_output;
  reg        [15:0]   pwm_pwm_area_channels_4_counter_map;
  wire       [1:0]    switch_Misc_l211_5;
  wire                pwm_pwm_area_channels_5_output;
  reg        [15:0]   pwm_pwm_area_channels_5_counter_map;
  wire       [1:0]    switch_Misc_l211_6;
  wire                pwm_pwm_area_channels_6_output;
  reg        [15:0]   pwm_pwm_area_channels_6_counter_map;
  wire       [1:0]    switch_Misc_l211_7;
  wire                pwm_pwm_area_channels_7_output;
  reg        [15:0]   pwm_pwm_area_channels_7_counter_map;
  reg        [31:0]   pwm_pwm_area_timeout_area_counter;
  wire                pwm_pwm_area_timeout_area_enable;
  reg                 pwm_pwm_area_timeout_area_flag;
  reg                 pwm_pwm_area_timeout_area_lastwdt;
  reg                 pwm_pwm_area_timeout_area_wdt_change;
  wire                when_PWM_l117;
  wire                when_PWM_l123;
  wire                when_PWM_l17;
  wire                when_PWM_l17_1;
  wire                pwm_pwm_area_output_enable;
  wire                _zz_pwm_pwm_out_ch0;
  reg        [1:0]    apb_operation_phase;
  reg                 apb_operation_transfer;
  reg                 apb_m_PWRITE_regNext;
  wire                spi_fsm_wantExit;
  reg                 spi_fsm_wantStart;
  wire                spi_fsm_wantKill;
  reg        [6:0]    spi_fsm_reg_addr;
  reg                 spi_fsm_being_written_fsm_wantExit;
  reg                 spi_fsm_being_written_fsm_wantStart;
  wire                spi_fsm_being_written_fsm_wantKill;
  reg        [7:0]    spi_fsm_being_written_fsm_data;
  reg        [6:0]    spi_fsm_being_written_fsm_ptr;
  reg                 spi_fsm_being_written_fsm_is_high_8bit;
  reg                 spi_fsm_sclk_count_willIncrement;
  reg                 spi_fsm_sclk_count_willClear;
  reg        [2:0]    spi_fsm_sclk_count_valueNext;
  reg        [2:0]    spi_fsm_sclk_count_value;
  wire                spi_fsm_sclk_count_willOverflowIfInc;
  wire                spi_fsm_sclk_count_willOverflow;
  reg                 spi_fsm_sclk_cnt_start;
  reg        [7:0]    spi_fsm_temp_rx;
  reg                 sclk_sync_regNext;
  wire                when_SPI_PWM_l90;
  reg        [1:0]    spi_fsm_being_written_fsm_stateReg;
  reg        [1:0]    spi_fsm_being_written_fsm_stateNext;
  wire       [7:0]    _zz_spi_fsm_being_written_fsm_data;
  reg        [3:0]    _zz_2;
  reg                 spi_fsm_being_written_fsm_is_high_8bit_regNext;
  wire                when_SPI_PWM_l154;
  reg        [1:0]    spi_fsm_stateReg;
  reg        [1:0]    spi_fsm_stateNext;
  wire                _zz_when_StateMachine_l229;
  wire                _zz_when_StateMachine_l229_1;
  wire                when_utils_l25;
  wire                when_utils_l25_1;
  wire                when_utils_l25_2;
  wire                when_utils_l25_3;
  wire                when_utils_l26;
  wire                when_utils_l34;
  reg        [15:0]   _zz_apb_m_PWDATA;
  wire                when_utils_l25_4;
  wire                when_utils_l25_5;
  wire                when_utils_l25_6;
  wire                when_utils_l26_1;
  wire                when_utils_l25_7;
  wire       [6:0]    switch_Regs_l47;
  reg        [3:0]    _zz_apb_m_PWDATA_1;
  wire                when_utils_l26_2;
  wire                when_utils_l25_8;
  wire                when_utils_l25_9;
  wire                when_utils_l25_10;
  wire                when_utils_l26_3;
  wire                when_utils_l25_11;
  wire                when_SPI_PWM_l111;
  wire                when_utils_l26_4;
  wire                when_utils_l34_1;
  wire                when_StateMachine_l229;
  wire                when_StateMachine_l245;
  wire                when_StateMachine_l245_1;
  wire                when_SPI_PWM_l63;
  `ifndef SYNTHESIS
  reg [63:0] apb_operation_phase_string;
  reg [47:0] spi_fsm_being_written_fsm_stateReg_string;
  reg [47:0] spi_fsm_being_written_fsm_stateNext_string;
  reg [111:0] spi_fsm_stateReg_string;
  reg [111:0] spi_fsm_stateNext_string;
  `endif

  (* ram_style = "distributed" *) reg [15:0] regs_data [0:15];

  assign _zz_pwm_pwm_area_channels_0_output_1 = {3'd0, _zz_pwm_pwm_area_channels_0_output};
  assign _zz_pwm_pwm_area_channels_1_output_1 = {2'd0, _zz_pwm_pwm_area_channels_1_output};
  assign _zz_pwm_pwm_area_channels_2_output_1 = {2'd0, _zz_pwm_pwm_area_channels_2_output};
  assign _zz_pwm_pwm_area_channels_3_output_1 = {1'd0, _zz_pwm_pwm_area_channels_3_output};
  assign _zz_pwm_pwm_area_channels_4_output_1 = {1'd0, _zz_pwm_pwm_area_channels_4_output};
  assign _zz_pwm_pwm_area_channels_5_output_1 = {1'd0, _zz_pwm_pwm_area_channels_5_output};
  assign _zz_pwm_pwm_area_channels_6_output_1 = {1'd0, _zz_pwm_pwm_area_channels_6_output};
  assign _zz_spi_fsm_sclk_count_valueNext_1 = spi_fsm_sclk_count_willIncrement;
  assign _zz_spi_fsm_sclk_count_valueNext = {2'd0, _zz_spi_fsm_sclk_count_valueNext_1};
  assign _zz_spi_fsm_temp_rx = {spi_fsm_temp_rx,mosi_sync};
  assign _zz_apb_m_PWDATA_2 = _zz_apb_m_PWDATA[15 : 8];
  assign _zz_apb_m_PWDATA_3 = _zz_apb_m_PWDATA[7 : 0];
  assign _zz__zz_pwm_sub_pwms_0_period_buf = 4'b0000;
  assign _zz_pwm_pwm_area_channels_7_output = 4'b1000;
  assign _zz__zz_switch_Misc_l211 = 4'b1001;
  assign _zz__zz_when_PWM_l17 = 4'b1010;
  assign _zz_pwm_pwm_regs_config = 4'b1011;
  assign _zz_pwm_pwm_regs_watchdog = 4'b1100;
  assign _zz_pwm_pwm_regs_timeout_max_high = 4'b1101;
  assign _zz_pwm_pwm_regs_timeout_max_low = 4'b1110;
  assign _zz_regs_data_port_15 = {spi_fsm_being_written_fsm_data,_zz_spi_fsm_being_written_fsm_data};
  initial begin
    $readmemb("E:\\projects\\I2C_PWM\\tmp\\job_1\\SPI_PWM_Top.v_toplevel_spi_pwm_1_regs_data.bin",regs_data);
  end
  assign _zz_regs_data_port0 = regs_data[_zz__zz_pwm_sub_pwms_0_period_buf];
  assign _zz_regs_data_port1 = regs_data[_zz_pwm_pwm_area_channels_0_output_1];
  assign _zz_regs_data_port2 = regs_data[_zz_pwm_pwm_area_channels_1_output_1];
  assign _zz_regs_data_port3 = regs_data[_zz_pwm_pwm_area_channels_2_output_1];
  assign _zz_regs_data_port4 = regs_data[_zz_pwm_pwm_area_channels_3_output_1];
  assign _zz_regs_data_port5 = regs_data[_zz_pwm_pwm_area_channels_4_output_1];
  assign _zz_regs_data_port6 = regs_data[_zz_pwm_pwm_area_channels_5_output_1];
  assign _zz_regs_data_port7 = regs_data[_zz_pwm_pwm_area_channels_6_output_1];
  assign _zz_regs_data_port8 = regs_data[_zz_pwm_pwm_area_channels_7_output];
  assign _zz_regs_data_port9 = regs_data[_zz__zz_switch_Misc_l211];
  assign _zz_regs_data_port10 = regs_data[_zz__zz_when_PWM_l17];
  assign _zz_regs_data_port11 = regs_data[_zz_pwm_pwm_regs_config];
  assign _zz_regs_data_port12 = regs_data[_zz_pwm_pwm_regs_watchdog];
  assign _zz_regs_data_port13 = regs_data[_zz_pwm_pwm_regs_timeout_max_high];
  assign _zz_regs_data_port14 = regs_data[_zz_pwm_pwm_regs_timeout_max_low];
  always @(posedge oscout) begin
    if(_zz_1) begin
      regs_data[_zz_2] <= _zz_regs_data_port_15;
    end
  end

  assign _zz_regs_data_port16 = regs_data[_zz_apb_m_PWDATA_1];
  BufferCC sclk_buffercc (
    .io_dataIn  (sclk                    ), //i
    .io_dataOut (sclk_buffercc_io_dataOut), //o
    .oscout     (oscout                  ), //i
    .reset      (reset                   )  //i
  );
  BufferCC mosi_buffercc (
    .io_dataIn  (mosi                    ), //i
    .io_dataOut (mosi_buffercc_io_dataOut), //o
    .oscout     (oscout                  ), //i
    .reset      (reset                   )  //i
  );
  BufferCC ss_buffercc (
    .io_dataIn  (ss                    ), //i
    .io_dataOut (ss_buffercc_io_dataOut), //o
    .oscout     (oscout                ), //i
    .reset      (reset                 )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(apb_operation_phase)
      APB3Phase_IDLE : apb_operation_phase_string = "IDLE    ";
      APB3Phase_SETUP : apb_operation_phase_string = "SETUP   ";
      APB3Phase_ACCESS_1 : apb_operation_phase_string = "ACCESS_1";
      default : apb_operation_phase_string = "????????";
    endcase
  end
  always @(*) begin
    case(spi_fsm_being_written_fsm_stateReg)
      spi_fsm_being_written_fsm_enumDef_BOOT : spi_fsm_being_written_fsm_stateReg_string = "BOOT  ";
      spi_fsm_being_written_fsm_enumDef_init : spi_fsm_being_written_fsm_stateReg_string = "init  ";
      spi_fsm_being_written_fsm_enumDef_wait_s : spi_fsm_being_written_fsm_stateReg_string = "wait_s";
      spi_fsm_being_written_fsm_enumDef_read : spi_fsm_being_written_fsm_stateReg_string = "read  ";
      default : spi_fsm_being_written_fsm_stateReg_string = "??????";
    endcase
  end
  always @(*) begin
    case(spi_fsm_being_written_fsm_stateNext)
      spi_fsm_being_written_fsm_enumDef_BOOT : spi_fsm_being_written_fsm_stateNext_string = "BOOT  ";
      spi_fsm_being_written_fsm_enumDef_init : spi_fsm_being_written_fsm_stateNext_string = "init  ";
      spi_fsm_being_written_fsm_enumDef_wait_s : spi_fsm_being_written_fsm_stateNext_string = "wait_s";
      spi_fsm_being_written_fsm_enumDef_read : spi_fsm_being_written_fsm_stateNext_string = "read  ";
      default : spi_fsm_being_written_fsm_stateNext_string = "??????";
    endcase
  end
  always @(*) begin
    case(spi_fsm_stateReg)
      spi_fsm_enumDef_BOOT : spi_fsm_stateReg_string = "BOOT          ";
      spi_fsm_enumDef_idle : spi_fsm_stateReg_string = "idle          ";
      spi_fsm_enumDef_being_written : spi_fsm_stateReg_string = "being_written ";
      spi_fsm_enumDef_start_transfer : spi_fsm_stateReg_string = "start_transfer";
      default : spi_fsm_stateReg_string = "??????????????";
    endcase
  end
  always @(*) begin
    case(spi_fsm_stateNext)
      spi_fsm_enumDef_BOOT : spi_fsm_stateNext_string = "BOOT          ";
      spi_fsm_enumDef_idle : spi_fsm_stateNext_string = "idle          ";
      spi_fsm_enumDef_being_written : spi_fsm_stateNext_string = "being_written ";
      spi_fsm_enumDef_start_transfer : spi_fsm_stateNext_string = "start_transfer";
      default : spi_fsm_stateNext_string = "??????????????";
    endcase
  end
  `endif

  always @(*) begin
    _zz_1 = 1'b0;
    case(spi_fsm_being_written_fsm_stateReg)
      spi_fsm_being_written_fsm_enumDef_init : begin
      end
      spi_fsm_being_written_fsm_enumDef_wait_s : begin
      end
      spi_fsm_being_written_fsm_enumDef_read : begin
        if(apb_m_PENABLE) begin
          _zz_1 = 1'b1;
        end
      end
      default : begin
      end
    endcase
  end

  assign sclk_sync = sclk_buffercc_io_dataOut;
  assign mosi_sync = mosi_buffercc_io_dataOut;
  assign ss_sync = ss_buffercc_io_dataOut;
  assign spi_slave_regs_data = 32'h0;
  assign spi_slave_regs_status = 32'h00000004;
  assign spi_slave_regs_config = 32'h00000008;
  always @(*) begin
    regs_outRange = 1'b0;
    case(spi_fsm_being_written_fsm_stateReg)
      spi_fsm_being_written_fsm_enumDef_init : begin
      end
      spi_fsm_being_written_fsm_enumDef_wait_s : begin
      end
      spi_fsm_being_written_fsm_enumDef_read : begin
        if(apb_m_PENABLE) begin
          case(spi_fsm_being_written_fsm_ptr)
            7'h0 : begin
            end
            7'h01 : begin
            end
            7'h02 : begin
            end
            7'h03 : begin
            end
            7'h04 : begin
            end
            7'h05 : begin
            end
            7'h06 : begin
            end
            7'h07 : begin
            end
            7'h08 : begin
            end
            7'h20 : begin
            end
            7'h40 : begin
            end
            7'h50 : begin
            end
            7'h7f : begin
            end
            7'h7e : begin
            end
            7'h7d : begin
            end
            default : begin
              regs_outRange = 1'b1;
            end
          endcase
        end
      end
      default : begin
      end
    endcase
    case(spi_fsm_stateReg)
      spi_fsm_enumDef_idle : begin
      end
      spi_fsm_enumDef_being_written : begin
      end
      spi_fsm_enumDef_start_transfer : begin
        if(when_utils_l25_7) begin
          case(switch_Regs_l47)
            7'h0 : begin
            end
            7'h01 : begin
            end
            7'h02 : begin
            end
            7'h03 : begin
            end
            7'h04 : begin
            end
            7'h05 : begin
            end
            7'h06 : begin
            end
            7'h07 : begin
            end
            7'h08 : begin
            end
            7'h20 : begin
            end
            7'h40 : begin
            end
            7'h50 : begin
            end
            7'h7f : begin
            end
            7'h7e : begin
            end
            7'h7d : begin
            end
            default : begin
              regs_outRange = 1'b1;
            end
          endcase
        end
      end
      default : begin
      end
    endcase
  end

  assign _zz_pwm_pwm_area_channels_0_output = 1'b1;
  assign _zz_pwm_pwm_area_channels_1_output = 2'b10;
  assign _zz_pwm_pwm_area_channels_2_output = 2'b11;
  assign _zz_pwm_pwm_area_channels_3_output = 3'b100;
  assign _zz_pwm_pwm_area_channels_4_output = 3'b101;
  assign _zz_pwm_pwm_area_channels_5_output = 3'b110;
  assign _zz_pwm_pwm_area_channels_6_output = 3'b111;
  assign _zz_switch_Misc_l211 = _zz_regs_data_port9;
  assign pwm_pwm_regs_config = _zz_regs_data_port11;
  assign pwm_pwm_regs_watchdog = _zz_regs_data_port12;
  assign pwm_pwm_regs_timeout_max_high = _zz_regs_data_port13;
  assign pwm_pwm_regs_timeout_max_low = _zz_regs_data_port14;
  assign when_PWM_l19 = 1'b1;
  assign _zz_pwm_sub_pwms_0_period_buf = _zz_regs_data_port0;
  assign when_PWM_l19_1 = pwm_pwm_regs_config[14];
  assign _zz_when_PWM_l17 = _zz_regs_data_port10;
  assign pwm_pre_divicder_divider_val = pwm_pwm_regs_config[4 : 0];
  always @(*) begin
    pwm_pre_divicder_clk_en = 1'b0;
    if(when_PWM_l93) begin
      pwm_pre_divicder_clk_en = 1'b1;
    end
  end

  assign when_PWM_l93 = (pwm_pre_divicder_counter == pwm_pre_divicder_divider_val);
  assign when_PWM_l96 = (pwm_pre_divicder_counter == pwm_pre_divicder_divider_val);
  assign switch_Misc_l211 = _zz_switch_Misc_l211[1 : 0];
  always @(*) begin
    case(switch_Misc_l211)
      2'b00 : begin
        pwm_pwm_area_channels_0_counter_map = pwm_sub_pwms_0_counter;
      end
      2'b01 : begin
        pwm_pwm_area_channels_0_counter_map = _zz_pwm_pwm_area_channels_0_counter_map;
      end
      default : begin
        pwm_pwm_area_channels_0_counter_map = 16'h0;
      end
    endcase
  end

  assign pwm_pwm_area_channels_0_output = (pwm_pwm_area_channels_0_counter_map < _zz_regs_data_port1);
  assign switch_Misc_l211_1 = _zz_switch_Misc_l211[3 : 2];
  always @(*) begin
    case(switch_Misc_l211_1)
      2'b00 : begin
        pwm_pwm_area_channels_1_counter_map = pwm_sub_pwms_0_counter;
      end
      2'b01 : begin
        pwm_pwm_area_channels_1_counter_map = _zz_pwm_pwm_area_channels_0_counter_map;
      end
      default : begin
        pwm_pwm_area_channels_1_counter_map = 16'h0;
      end
    endcase
  end

  assign pwm_pwm_area_channels_1_output = (pwm_pwm_area_channels_1_counter_map < _zz_regs_data_port2);
  assign switch_Misc_l211_2 = _zz_switch_Misc_l211[5 : 4];
  always @(*) begin
    case(switch_Misc_l211_2)
      2'b00 : begin
        pwm_pwm_area_channels_2_counter_map = pwm_sub_pwms_0_counter;
      end
      2'b01 : begin
        pwm_pwm_area_channels_2_counter_map = _zz_pwm_pwm_area_channels_0_counter_map;
      end
      default : begin
        pwm_pwm_area_channels_2_counter_map = 16'h0;
      end
    endcase
  end

  assign pwm_pwm_area_channels_2_output = (pwm_pwm_area_channels_2_counter_map < _zz_regs_data_port3);
  assign switch_Misc_l211_3 = _zz_switch_Misc_l211[7 : 6];
  always @(*) begin
    case(switch_Misc_l211_3)
      2'b00 : begin
        pwm_pwm_area_channels_3_counter_map = pwm_sub_pwms_0_counter;
      end
      2'b01 : begin
        pwm_pwm_area_channels_3_counter_map = _zz_pwm_pwm_area_channels_0_counter_map;
      end
      default : begin
        pwm_pwm_area_channels_3_counter_map = 16'h0;
      end
    endcase
  end

  assign pwm_pwm_area_channels_3_output = (pwm_pwm_area_channels_3_counter_map < _zz_regs_data_port4);
  assign switch_Misc_l211_4 = _zz_switch_Misc_l211[9 : 8];
  always @(*) begin
    case(switch_Misc_l211_4)
      2'b00 : begin
        pwm_pwm_area_channels_4_counter_map = pwm_sub_pwms_0_counter;
      end
      2'b01 : begin
        pwm_pwm_area_channels_4_counter_map = _zz_pwm_pwm_area_channels_0_counter_map;
      end
      default : begin
        pwm_pwm_area_channels_4_counter_map = 16'h0;
      end
    endcase
  end

  assign pwm_pwm_area_channels_4_output = (pwm_pwm_area_channels_4_counter_map < _zz_regs_data_port5);
  assign switch_Misc_l211_5 = _zz_switch_Misc_l211[11 : 10];
  always @(*) begin
    case(switch_Misc_l211_5)
      2'b00 : begin
        pwm_pwm_area_channels_5_counter_map = pwm_sub_pwms_0_counter;
      end
      2'b01 : begin
        pwm_pwm_area_channels_5_counter_map = _zz_pwm_pwm_area_channels_0_counter_map;
      end
      default : begin
        pwm_pwm_area_channels_5_counter_map = 16'h0;
      end
    endcase
  end

  assign pwm_pwm_area_channels_5_output = (pwm_pwm_area_channels_5_counter_map < _zz_regs_data_port6);
  assign switch_Misc_l211_6 = _zz_switch_Misc_l211[13 : 12];
  always @(*) begin
    case(switch_Misc_l211_6)
      2'b00 : begin
        pwm_pwm_area_channels_6_counter_map = pwm_sub_pwms_0_counter;
      end
      2'b01 : begin
        pwm_pwm_area_channels_6_counter_map = _zz_pwm_pwm_area_channels_0_counter_map;
      end
      default : begin
        pwm_pwm_area_channels_6_counter_map = 16'h0;
      end
    endcase
  end

  assign pwm_pwm_area_channels_6_output = (pwm_pwm_area_channels_6_counter_map < _zz_regs_data_port7);
  assign switch_Misc_l211_7 = _zz_switch_Misc_l211[15 : 14];
  always @(*) begin
    case(switch_Misc_l211_7)
      2'b00 : begin
        pwm_pwm_area_channels_7_counter_map = pwm_sub_pwms_0_counter;
      end
      2'b01 : begin
        pwm_pwm_area_channels_7_counter_map = _zz_pwm_pwm_area_channels_0_counter_map;
      end
      default : begin
        pwm_pwm_area_channels_7_counter_map = 16'h0;
      end
    endcase
  end

  assign pwm_pwm_area_channels_7_output = (pwm_pwm_area_channels_7_counter_map < _zz_regs_data_port8);
  assign pwm_pwm_area_timeout_area_enable = pwm_pwm_regs_config[15];
  assign when_PWM_l117 = (pwm_pwm_area_timeout_area_enable && (! pwm_pwm_area_timeout_area_flag));
  assign when_PWM_l123 = (pwm_pwm_area_timeout_area_counter == {pwm_pwm_regs_timeout_max_high,pwm_pwm_regs_timeout_max_low});
  assign when_PWM_l17 = ((pwm_sub_pwms_0_counter == _zz_pwm_sub_pwms_0_period_buf) || (pwm_sub_pwms_0_period_buf != _zz_pwm_sub_pwms_0_period_buf));
  assign when_PWM_l17_1 = ((_zz_pwm_pwm_area_channels_0_counter_map == _zz_when_PWM_l17) || (_zz_when_PWM_l17_1 != _zz_when_PWM_l17));
  assign pwm_pwm_area_output_enable = 1'b1;
  assign _zz_pwm_pwm_out_ch0 = (! (pwm_pwm_area_timeout_area_enable && pwm_pwm_area_timeout_area_flag));
  assign pwm_pwm_out_ch0 = (pwm_pwm_area_channels_0_output && _zz_pwm_pwm_out_ch0);
  assign pwm_pwm_out_ch1 = (pwm_pwm_area_channels_1_output && _zz_pwm_pwm_out_ch0);
  assign pwm_pwm_out_ch2 = (pwm_pwm_area_channels_2_output && _zz_pwm_pwm_out_ch0);
  assign pwm_pwm_out_ch3 = (pwm_pwm_area_channels_3_output && _zz_pwm_pwm_out_ch0);
  assign pwm_pwm_out_ch4 = (pwm_pwm_area_channels_4_output && _zz_pwm_pwm_out_ch0);
  assign pwm_pwm_out_ch5 = (pwm_pwm_area_channels_5_output && _zz_pwm_pwm_out_ch0);
  assign pwm_pwm_out_ch6 = (pwm_pwm_area_channels_6_output && _zz_pwm_pwm_out_ch0);
  assign pwm_pwm_out_ch7 = (pwm_pwm_area_channels_7_output && _zz_pwm_pwm_out_ch0);
  always @(*) begin
    apb_operation_transfer = 1'b0;
    case(spi_fsm_being_written_fsm_stateReg)
      spi_fsm_being_written_fsm_enumDef_init : begin
        apb_operation_transfer = 1'b1;
        if(apb_m_PENABLE) begin
          apb_operation_transfer = 1'b0;
        end
      end
      spi_fsm_being_written_fsm_enumDef_wait_s : begin
      end
      spi_fsm_being_written_fsm_enumDef_read : begin
        apb_operation_transfer = 1'b1;
        if(apb_m_PENABLE) begin
          apb_operation_transfer = 1'b0;
        end
      end
      default : begin
      end
    endcase
    case(spi_fsm_stateReg)
      spi_fsm_enumDef_idle : begin
        if(when_utils_l25) begin
          apb_operation_transfer = 1'b1;
          if(apb_m_PENABLE) begin
            apb_operation_transfer = 1'b0;
          end
        end
        if(when_utils_l25_1) begin
          apb_operation_transfer = 1'b1;
          if(apb_m_PENABLE) begin
            apb_operation_transfer = 1'b0;
          end
        end
      end
      spi_fsm_enumDef_being_written : begin
      end
      spi_fsm_enumDef_start_transfer : begin
        if(when_utils_l25_4) begin
          apb_operation_transfer = 1'b1;
          if(apb_m_PENABLE) begin
            apb_operation_transfer = 1'b0;
          end
        end
        if(when_utils_l25_5) begin
          apb_operation_transfer = 1'b1;
          if(apb_m_PENABLE) begin
            apb_operation_transfer = 1'b0;
          end
        end
        if(when_utils_l25_8) begin
          apb_operation_transfer = 1'b1;
          if(apb_m_PENABLE) begin
            apb_operation_transfer = 1'b0;
          end
        end
        if(when_utils_l25_9) begin
          apb_operation_transfer = 1'b1;
          if(apb_m_PENABLE) begin
            apb_operation_transfer = 1'b0;
          end
        end
      end
      default : begin
      end
    endcase
  end

  always @(*) begin
    apb_m_PENABLE = 1'b0;
    case(apb_operation_phase)
      APB3Phase_IDLE : begin
        apb_m_PENABLE = 1'b0;
      end
      APB3Phase_SETUP : begin
      end
      default : begin
        apb_m_PENABLE = 1'b1;
      end
    endcase
  end

  always @(*) begin
    apb_m_PWDATA = 32'h0;
    case(spi_fsm_being_written_fsm_stateReg)
      spi_fsm_being_written_fsm_enumDef_init : begin
        apb_m_PWDATA = 32'h00008002;
      end
      spi_fsm_being_written_fsm_enumDef_wait_s : begin
      end
      spi_fsm_being_written_fsm_enumDef_read : begin
      end
      default : begin
      end
    endcase
    case(spi_fsm_stateReg)
      spi_fsm_enumDef_idle : begin
        if(when_utils_l25) begin
          apb_m_PWDATA = 32'h00002004;
        end
        if(when_utils_l25_1) begin
          apb_m_PWDATA = 32'h0;
        end
      end
      spi_fsm_enumDef_being_written : begin
      end
      spi_fsm_enumDef_start_transfer : begin
        if(when_utils_l25_4) begin
          apb_m_PWDATA = 32'h00001000;
        end
        if(when_utils_l25_5) begin
          apb_m_PWDATA = 32'h000000ff;
        end
        if(when_utils_l25_8) begin
          apb_m_PWDATA = {24'd0, _zz_apb_m_PWDATA_2};
        end
        if(when_utils_l25_9) begin
          apb_m_PWDATA = {24'd0, _zz_apb_m_PWDATA_3};
        end
      end
      default : begin
      end
    endcase
  end

  always @(*) begin
    case(apb_operation_phase)
      APB3Phase_IDLE : begin
        apb_m_PSEL = 1'b0;
      end
      APB3Phase_SETUP : begin
        apb_m_PSEL = 1'b1;
      end
      default : begin
        apb_m_PSEL = 1'b1;
      end
    endcase
  end

  assign spi_fsm_wantExit = 1'b0;
  always @(*) begin
    spi_fsm_wantStart = 1'b0;
    case(spi_fsm_stateReg)
      spi_fsm_enumDef_idle : begin
      end
      spi_fsm_enumDef_being_written : begin
      end
      spi_fsm_enumDef_start_transfer : begin
      end
      default : begin
        spi_fsm_wantStart = 1'b1;
      end
    endcase
  end

  assign spi_fsm_wantKill = 1'b0;
  always @(*) begin
    spi_fsm_being_written_fsm_wantExit = 1'b0;
    case(spi_fsm_being_written_fsm_stateReg)
      spi_fsm_being_written_fsm_enumDef_init : begin
      end
      spi_fsm_being_written_fsm_enumDef_wait_s : begin
        if(!interrupt) begin
          if(ss_sync) begin
            spi_fsm_being_written_fsm_wantExit = 1'b1;
          end
        end
      end
      spi_fsm_being_written_fsm_enumDef_read : begin
      end
      default : begin
      end
    endcase
  end

  always @(*) begin
    spi_fsm_being_written_fsm_wantStart = 1'b0;
    if(when_StateMachine_l245) begin
      spi_fsm_being_written_fsm_wantStart = 1'b1;
    end
  end

  assign spi_fsm_being_written_fsm_wantKill = 1'b0;
  always @(*) begin
    spi_fsm_sclk_count_willIncrement = 1'b0;
    if(when_SPI_PWM_l90) begin
      spi_fsm_sclk_count_willIncrement = 1'b1;
    end
  end

  always @(*) begin
    spi_fsm_sclk_count_willClear = 1'b0;
    if(when_StateMachine_l229) begin
      spi_fsm_sclk_count_willClear = 1'b1;
    end
  end

  assign spi_fsm_sclk_count_willOverflowIfInc = (spi_fsm_sclk_count_value == 3'b111);
  assign spi_fsm_sclk_count_willOverflow = (spi_fsm_sclk_count_willOverflowIfInc && spi_fsm_sclk_count_willIncrement);
  always @(*) begin
    spi_fsm_sclk_count_valueNext = (spi_fsm_sclk_count_value + _zz_spi_fsm_sclk_count_valueNext);
    if(spi_fsm_sclk_count_willClear) begin
      spi_fsm_sclk_count_valueNext = 3'b000;
    end
  end

  assign when_SPI_PWM_l90 = (spi_fsm_sclk_cnt_start && (sclk_sync && (! sclk_sync_regNext)));
  always @(*) begin
    spi_fsm_being_written_fsm_stateNext = spi_fsm_being_written_fsm_stateReg;
    case(spi_fsm_being_written_fsm_stateReg)
      spi_fsm_being_written_fsm_enumDef_init : begin
        if(apb_m_PENABLE) begin
          spi_fsm_being_written_fsm_stateNext = spi_fsm_being_written_fsm_enumDef_wait_s;
        end
      end
      spi_fsm_being_written_fsm_enumDef_wait_s : begin
        if(interrupt) begin
          spi_fsm_being_written_fsm_stateNext = spi_fsm_being_written_fsm_enumDef_read;
        end else begin
          if(ss_sync) begin
            spi_fsm_being_written_fsm_stateNext = spi_fsm_being_written_fsm_enumDef_BOOT;
          end
        end
      end
      spi_fsm_being_written_fsm_enumDef_read : begin
        if(apb_m_PENABLE) begin
          spi_fsm_being_written_fsm_stateNext = spi_fsm_being_written_fsm_enumDef_wait_s;
        end
      end
      default : begin
      end
    endcase
    if(spi_fsm_being_written_fsm_wantStart) begin
      spi_fsm_being_written_fsm_stateNext = spi_fsm_being_written_fsm_enumDef_init;
    end
    if(spi_fsm_being_written_fsm_wantKill) begin
      spi_fsm_being_written_fsm_stateNext = spi_fsm_being_written_fsm_enumDef_BOOT;
    end
  end

  assign _zz_spi_fsm_being_written_fsm_data = apb_m_PRDATA[7 : 0];
  always @(*) begin
    case(spi_fsm_being_written_fsm_ptr)
      7'h0 : begin
        _zz_2 = 4'b0000;
      end
      7'h01 : begin
        _zz_2 = 4'b0001;
      end
      7'h02 : begin
        _zz_2 = 4'b0010;
      end
      7'h03 : begin
        _zz_2 = 4'b0011;
      end
      7'h04 : begin
        _zz_2 = 4'b0100;
      end
      7'h05 : begin
        _zz_2 = 4'b0101;
      end
      7'h06 : begin
        _zz_2 = 4'b0110;
      end
      7'h07 : begin
        _zz_2 = 4'b0111;
      end
      7'h08 : begin
        _zz_2 = 4'b1000;
      end
      7'h20 : begin
        _zz_2 = 4'b1001;
      end
      7'h40 : begin
        _zz_2 = 4'b1010;
      end
      7'h50 : begin
        _zz_2 = 4'b1011;
      end
      7'h7f : begin
        _zz_2 = 4'b1100;
      end
      7'h7e : begin
        _zz_2 = 4'b1101;
      end
      7'h7d : begin
        _zz_2 = 4'b1110;
      end
      default : begin
        _zz_2 = 4'b0000;
      end
    endcase
  end

  assign when_SPI_PWM_l154 = (spi_fsm_being_written_fsm_is_high_8bit && (! spi_fsm_being_written_fsm_is_high_8bit_regNext));
  assign _zz_when_StateMachine_l229 = (spi_fsm_stateReg == spi_fsm_enumDef_start_transfer);
  assign _zz_when_StateMachine_l229_1 = (spi_fsm_stateNext == spi_fsm_enumDef_start_transfer);
  always @(*) begin
    spi_fsm_stateNext = spi_fsm_stateReg;
    case(spi_fsm_stateReg)
      spi_fsm_enumDef_idle : begin
        if(when_utils_l25_3) begin
          spi_fsm_stateNext = spi_fsm_enumDef_start_transfer;
        end
      end
      spi_fsm_enumDef_being_written : begin
        if(spi_fsm_being_written_fsm_wantExit) begin
          spi_fsm_stateNext = spi_fsm_enumDef_idle;
        end
      end
      spi_fsm_enumDef_start_transfer : begin
        if(when_utils_l25_11) begin
          if(when_SPI_PWM_l111) begin
            spi_fsm_stateNext = spi_fsm_enumDef_being_written;
          end else begin
            spi_fsm_stateNext = spi_fsm_enumDef_idle;
          end
        end
      end
      default : begin
      end
    endcase
    if(when_SPI_PWM_l63) begin
      spi_fsm_stateNext = spi_fsm_enumDef_idle;
    end
    if(spi_fsm_wantStart) begin
      spi_fsm_stateNext = spi_fsm_enumDef_idle;
    end
    if(spi_fsm_wantKill) begin
      spi_fsm_stateNext = spi_fsm_enumDef_BOOT;
    end
  end

  assign when_utils_l25 = (_zz_when_utils_l25_1 == 3'b000);
  assign when_utils_l25_1 = (_zz_when_utils_l25_1 == 3'b001);
  assign when_utils_l25_2 = (_zz_when_utils_l25_1 == 3'b010);
  assign when_utils_l25_3 = (_zz_when_utils_l25_1 == 3'b011);
  assign when_utils_l26 = 1'b1;
  assign when_utils_l34 = (_zz_when_utils_l25_1 == 3'b100);
  assign when_utils_l25_4 = (_zz_when_utils_l25 == 4'b0000);
  assign when_utils_l25_5 = (_zz_when_utils_l25 == 4'b0001);
  assign when_utils_l25_6 = (_zz_when_utils_l25 == 4'b0010);
  assign when_utils_l26_1 = (spi_fsm_sclk_count_value == 3'b111);
  assign when_utils_l25_7 = (_zz_when_utils_l25 == 4'b0011);
  assign switch_Regs_l47 = spi_fsm_temp_rx[6 : 0];
  always @(*) begin
    case(switch_Regs_l47)
      7'h0 : begin
        _zz_apb_m_PWDATA_1 = 4'b0000;
      end
      7'h01 : begin
        _zz_apb_m_PWDATA_1 = 4'b0001;
      end
      7'h02 : begin
        _zz_apb_m_PWDATA_1 = 4'b0010;
      end
      7'h03 : begin
        _zz_apb_m_PWDATA_1 = 4'b0011;
      end
      7'h04 : begin
        _zz_apb_m_PWDATA_1 = 4'b0100;
      end
      7'h05 : begin
        _zz_apb_m_PWDATA_1 = 4'b0101;
      end
      7'h06 : begin
        _zz_apb_m_PWDATA_1 = 4'b0110;
      end
      7'h07 : begin
        _zz_apb_m_PWDATA_1 = 4'b0111;
      end
      7'h08 : begin
        _zz_apb_m_PWDATA_1 = 4'b1000;
      end
      7'h20 : begin
        _zz_apb_m_PWDATA_1 = 4'b1001;
      end
      7'h40 : begin
        _zz_apb_m_PWDATA_1 = 4'b1010;
      end
      7'h50 : begin
        _zz_apb_m_PWDATA_1 = 4'b1011;
      end
      7'h7f : begin
        _zz_apb_m_PWDATA_1 = 4'b1100;
      end
      7'h7e : begin
        _zz_apb_m_PWDATA_1 = 4'b1101;
      end
      7'h7d : begin
        _zz_apb_m_PWDATA_1 = 4'b1110;
      end
      default : begin
        _zz_apb_m_PWDATA_1 = 4'b0000;
      end
    endcase
  end

  assign when_utils_l26_2 = 1'b1;
  assign when_utils_l25_8 = (_zz_when_utils_l25 == 4'b0100);
  assign when_utils_l25_9 = (_zz_when_utils_l25 == 4'b0101);
  assign when_utils_l25_10 = (_zz_when_utils_l25 == 4'b0110);
  assign when_utils_l26_3 = (spi_fsm_sclk_count_value == 3'b000);
  assign when_utils_l25_11 = (_zz_when_utils_l25 == 4'b0111);
  assign when_SPI_PWM_l111 = spi_fsm_temp_rx[0];
  assign when_utils_l26_4 = 1'b1;
  assign when_utils_l34_1 = (_zz_when_utils_l25 == 4'b1000);
  assign when_StateMachine_l229 = (_zz_when_StateMachine_l229 && (! _zz_when_StateMachine_l229_1));
  assign when_StateMachine_l245 = ((! (spi_fsm_stateReg == spi_fsm_enumDef_being_written)) && (spi_fsm_stateNext == spi_fsm_enumDef_being_written));
  assign when_StateMachine_l245_1 = ((! _zz_when_StateMachine_l229) && _zz_when_StateMachine_l229_1);
  assign when_SPI_PWM_l63 = (ss_sync && (! (spi_fsm_stateReg == spi_fsm_enumDef_being_written)));
  always @(posedge oscout or posedge reset) begin
    if(reset) begin
      _zz_when_utils_l25 <= 4'b0000;
      _zz_when_utils_l25_1 <= 3'b000;
      pwm_sub_pwms_0_counter <= 16'h0;
      _zz_pwm_pwm_area_channels_0_counter_map <= 16'h0;
      pwm_pre_divicder_counter <= 5'h0;
      pwm_pwm_area_timeout_area_counter <= 32'h0;
      pwm_pwm_area_timeout_area_flag <= 1'b0;
      pwm_pwm_area_timeout_area_lastwdt <= 1'b0;
      apb_operation_phase <= APB3Phase_SETUP;
      spi_fsm_reg_addr <= 7'h0;
      spi_fsm_being_written_fsm_data <= 8'h0;
      spi_fsm_being_written_fsm_ptr <= 7'h0;
      spi_fsm_being_written_fsm_is_high_8bit <= 1'b1;
      spi_fsm_sclk_count_value <= 3'b000;
      spi_fsm_sclk_cnt_start <= 1'b0;
      spi_fsm_temp_rx <= 8'h0;
      spi_fsm_being_written_fsm_stateReg <= spi_fsm_being_written_fsm_enumDef_BOOT;
      spi_fsm_stateReg <= spi_fsm_enumDef_BOOT;
    end else begin
      pwm_pre_divicder_counter <= (pwm_pre_divicder_counter + 5'h01);
      if(when_PWM_l96) begin
        pwm_pre_divicder_counter <= 5'h0;
      end
      pwm_pwm_area_timeout_area_lastwdt <= pwm_pwm_regs_watchdog[0];
      if(when_PWM_l117) begin
        pwm_pwm_area_timeout_area_counter <= (pwm_pwm_area_timeout_area_counter + 32'h00000001);
      end else begin
        if(pwm_pwm_area_timeout_area_wdt_change) begin
          pwm_pwm_area_timeout_area_counter <= 32'h0;
        end
      end
      if(when_PWM_l123) begin
        pwm_pwm_area_timeout_area_flag <= 1'b1;
      end
      if(pwm_pwm_area_timeout_area_wdt_change) begin
        pwm_pwm_area_timeout_area_flag <= 1'b0;
      end
      if(pwm_pre_divicder_clk_en) begin
        if(when_PWM_l17) begin
          pwm_sub_pwms_0_counter <= 16'h0;
        end else begin
          if(when_PWM_l19) begin
            pwm_sub_pwms_0_counter <= (pwm_sub_pwms_0_counter + 16'h0001);
          end
        end
        if(when_PWM_l17_1) begin
          _zz_pwm_pwm_area_channels_0_counter_map <= 16'h0;
        end else begin
          if(when_PWM_l19_1) begin
            _zz_pwm_pwm_area_channels_0_counter_map <= (_zz_pwm_pwm_area_channels_0_counter_map + 16'h0001);
          end
        end
      end
      case(apb_operation_phase)
        APB3Phase_IDLE : begin
          if(apb_operation_transfer) begin
            apb_operation_phase <= APB3Phase_SETUP;
          end
        end
        APB3Phase_SETUP : begin
          if(apb_operation_transfer) begin
            apb_operation_phase <= APB3Phase_ACCESS_1;
          end
        end
        default : begin
          apb_operation_phase <= APB3Phase_SETUP;
        end
      endcase
      spi_fsm_sclk_count_value <= spi_fsm_sclk_count_valueNext;
      if(when_SPI_PWM_l90) begin
        spi_fsm_temp_rx <= _zz_spi_fsm_temp_rx[7:0];
      end
      spi_fsm_being_written_fsm_stateReg <= spi_fsm_being_written_fsm_stateNext;
      case(spi_fsm_being_written_fsm_stateReg)
        spi_fsm_being_written_fsm_enumDef_init : begin
          if(apb_m_PENABLE) begin
            spi_fsm_being_written_fsm_ptr <= spi_fsm_reg_addr;
          end
        end
        spi_fsm_being_written_fsm_enumDef_wait_s : begin
        end
        spi_fsm_being_written_fsm_enumDef_read : begin
          if(apb_m_PENABLE) begin
            spi_fsm_being_written_fsm_is_high_8bit <= (! spi_fsm_being_written_fsm_is_high_8bit);
            spi_fsm_being_written_fsm_data <= _zz_spi_fsm_being_written_fsm_data;
          end
        end
        default : begin
        end
      endcase
      if(when_SPI_PWM_l154) begin
        spi_fsm_being_written_fsm_ptr <= (spi_fsm_being_written_fsm_ptr + 7'h01);
      end
      spi_fsm_stateReg <= spi_fsm_stateNext;
      case(spi_fsm_stateReg)
        spi_fsm_enumDef_idle : begin
          if(when_utils_l25) begin
            if(apb_m_PENABLE) begin
              _zz_when_utils_l25_1 <= 3'b001;
            end
          end
          if(when_utils_l25_1) begin
            if(apb_m_PENABLE) begin
              _zz_when_utils_l25_1 <= 3'b010;
            end
          end
          if(when_utils_l25_2) begin
            if(interrupt) begin
              _zz_when_utils_l25_1 <= 3'b011;
            end
          end
          if(when_utils_l25_3) begin
            if(when_utils_l26) begin
              _zz_when_utils_l25_1 <= 3'b100;
            end
          end
          if(when_utils_l34) begin
            _zz_when_utils_l25_1 <= 3'b000;
          end
        end
        spi_fsm_enumDef_being_written : begin
        end
        spi_fsm_enumDef_start_transfer : begin
          if(when_utils_l25_4) begin
            if(apb_m_PENABLE) begin
              _zz_when_utils_l25 <= 4'b0001;
            end
          end
          if(when_utils_l25_5) begin
            if(apb_m_PENABLE) begin
              _zz_when_utils_l25 <= 4'b0010;
            end
          end
          if(when_utils_l25_6) begin
            if(when_utils_l26_1) begin
              _zz_when_utils_l25 <= 4'b0011;
            end
          end
          if(when_utils_l25_7) begin
            spi_fsm_reg_addr <= spi_fsm_temp_rx[6 : 0];
            if(when_utils_l26_2) begin
              _zz_when_utils_l25 <= 4'b0100;
            end
          end
          if(when_utils_l25_8) begin
            if(apb_m_PENABLE) begin
              _zz_when_utils_l25 <= 4'b0101;
            end
          end
          if(when_utils_l25_9) begin
            if(apb_m_PENABLE) begin
              _zz_when_utils_l25 <= 4'b0110;
            end
          end
          if(when_utils_l25_10) begin
            if(when_utils_l26_3) begin
              _zz_when_utils_l25 <= 4'b0111;
            end
          end
          if(when_utils_l25_11) begin
            if(when_utils_l26_4) begin
              _zz_when_utils_l25 <= 4'b1000;
            end
          end
          if(when_utils_l34_1) begin
            _zz_when_utils_l25 <= 4'b0000;
          end
        end
        default : begin
        end
      endcase
      if(when_StateMachine_l229) begin
        spi_fsm_sclk_cnt_start <= 1'b0;
      end
      if(when_StateMachine_l245_1) begin
        spi_fsm_sclk_cnt_start <= 1'b1;
        spi_fsm_temp_rx <= 8'h0;
      end
    end
  end

  always @(posedge oscout) begin
    pwm_sub_pwms_0_period_buf <= _zz_pwm_sub_pwms_0_period_buf;
    _zz_when_PWM_l17_1 <= _zz_when_PWM_l17;
    pwm_pwm_area_timeout_area_wdt_change <= (pwm_pwm_regs_watchdog[0] ^ pwm_pwm_area_timeout_area_lastwdt);
    apb_m_PWRITE_regNext <= apb_m_PWRITE;
    sclk_sync_regNext <= sclk_sync;
    case(spi_fsm_being_written_fsm_stateReg)
      spi_fsm_being_written_fsm_enumDef_init : begin
        apb_m_PWRITE <= 1'b1;
        apb_m_PADDR <= spi_slave_regs_status[7:0];
      end
      spi_fsm_being_written_fsm_enumDef_wait_s : begin
      end
      spi_fsm_being_written_fsm_enumDef_read : begin
        apb_m_PWRITE <= 1'b0;
        apb_m_PADDR <= spi_slave_regs_data[7:0];
      end
      default : begin
      end
    endcase
    spi_fsm_being_written_fsm_is_high_8bit_regNext <= spi_fsm_being_written_fsm_is_high_8bit;
    case(spi_fsm_stateReg)
      spi_fsm_enumDef_idle : begin
        if(when_utils_l25) begin
          apb_m_PWRITE <= 1'b1;
          apb_m_PADDR <= spi_slave_regs_status[7:0];
        end
        if(when_utils_l25_1) begin
          apb_m_PWRITE <= 1'b1;
          apb_m_PADDR <= spi_slave_regs_config[7:0];
        end
      end
      spi_fsm_enumDef_being_written : begin
      end
      spi_fsm_enumDef_start_transfer : begin
        if(when_utils_l25_4) begin
          apb_m_PWRITE <= 1'b1;
          apb_m_PADDR <= spi_slave_regs_status[7:0];
        end
        if(when_utils_l25_5) begin
          apb_m_PWRITE <= 1'b1;
          apb_m_PADDR <= spi_slave_regs_data[7:0];
        end
        if(when_utils_l25_8) begin
          apb_m_PWRITE <= 1'b1;
          apb_m_PADDR <= spi_slave_regs_data[7:0];
        end
        if(when_utils_l25_9) begin
          apb_m_PWRITE <= 1'b1;
          apb_m_PADDR <= spi_slave_regs_data[7:0];
        end
      end
      default : begin
      end
    endcase
  end

  always @(posedge oscout or posedge reset) begin
    if(reset) begin
      _zz_apb_m_PWDATA <= 16'h0;
    end else begin
      if(when_utils_l25_7) begin
        _zz_apb_m_PWDATA <= _zz_regs_data_port16;
      end
    end
  end


endmodule

//StreamFifo replaced by StreamFifo

module StreamFifo (
  input               io_push_valid,
  output              io_push_ready,
  input      [7:0]    io_push_payload,
  output              io_pop_valid,
  input               io_pop_ready,
  output     [7:0]    io_pop_payload,
  input               io_flush,
  output reg [1:0]    io_occupancy,
  output reg [1:0]    io_availability,
  input               oscout,
  input               reset
);

  reg        [7:0]    _zz_logic_ram_port0;
  wire       [1:0]    _zz_logic_pushPtr_valueNext;
  wire       [0:0]    _zz_logic_pushPtr_valueNext_1;
  wire       [1:0]    _zz_logic_popPtr_valueNext;
  wire       [0:0]    _zz_logic_popPtr_valueNext_1;
  wire                _zz_logic_ram_port;
  wire                _zz_io_pop_payload;
  wire       [1:0]    _zz_io_occupancy;
  wire       [1:0]    _zz_io_availability;
  wire       [1:0]    _zz_io_availability_1;
  wire       [1:0]    _zz_io_availability_2;
  reg                 _zz_1;
  reg                 logic_pushPtr_willIncrement;
  reg                 logic_pushPtr_willClear;
  reg        [1:0]    logic_pushPtr_valueNext;
  reg        [1:0]    logic_pushPtr_value;
  wire                logic_pushPtr_willOverflowIfInc;
  wire                logic_pushPtr_willOverflow;
  reg                 logic_popPtr_willIncrement;
  reg                 logic_popPtr_willClear;
  reg        [1:0]    logic_popPtr_valueNext;
  reg        [1:0]    logic_popPtr_value;
  wire                logic_popPtr_willOverflowIfInc;
  wire                logic_popPtr_willOverflow;
  wire                logic_ptrMatch;
  reg                 logic_risingOccupancy;
  wire                logic_pushing;
  wire                logic_popping;
  wire                logic_empty;
  wire                logic_full;
  reg                 _zz_io_pop_valid;
  wire                when_Stream_l1021;
  wire       [1:0]    logic_ptrDif;
  reg [7:0] logic_ram [0:2];

  assign _zz_logic_pushPtr_valueNext_1 = logic_pushPtr_willIncrement;
  assign _zz_logic_pushPtr_valueNext = {1'd0, _zz_logic_pushPtr_valueNext_1};
  assign _zz_logic_popPtr_valueNext_1 = logic_popPtr_willIncrement;
  assign _zz_logic_popPtr_valueNext = {1'd0, _zz_logic_popPtr_valueNext_1};
  assign _zz_io_occupancy = (2'b11 + logic_ptrDif);
  assign _zz_io_availability = (2'b11 + _zz_io_availability_1);
  assign _zz_io_availability_1 = (logic_popPtr_value - logic_pushPtr_value);
  assign _zz_io_availability_2 = (logic_popPtr_value - logic_pushPtr_value);
  assign _zz_io_pop_payload = 1'b1;
  always @(posedge oscout) begin
    if(_zz_io_pop_payload) begin
      _zz_logic_ram_port0 <= logic_ram[logic_popPtr_valueNext];
    end
  end

  always @(posedge oscout) begin
    if(_zz_1) begin
      logic_ram[logic_pushPtr_value] <= io_push_payload;
    end
  end

  always @(*) begin
    _zz_1 = 1'b0;
    if(logic_pushing) begin
      _zz_1 = 1'b1;
    end
  end

  always @(*) begin
    logic_pushPtr_willIncrement = 1'b0;
    if(logic_pushing) begin
      logic_pushPtr_willIncrement = 1'b1;
    end
  end

  always @(*) begin
    logic_pushPtr_willClear = 1'b0;
    if(io_flush) begin
      logic_pushPtr_willClear = 1'b1;
    end
  end

  assign logic_pushPtr_willOverflowIfInc = (logic_pushPtr_value == 2'b10);
  assign logic_pushPtr_willOverflow = (logic_pushPtr_willOverflowIfInc && logic_pushPtr_willIncrement);
  always @(*) begin
    if(logic_pushPtr_willOverflow) begin
      logic_pushPtr_valueNext = 2'b00;
    end else begin
      logic_pushPtr_valueNext = (logic_pushPtr_value + _zz_logic_pushPtr_valueNext);
    end
    if(logic_pushPtr_willClear) begin
      logic_pushPtr_valueNext = 2'b00;
    end
  end

  always @(*) begin
    logic_popPtr_willIncrement = 1'b0;
    if(logic_popping) begin
      logic_popPtr_willIncrement = 1'b1;
    end
  end

  always @(*) begin
    logic_popPtr_willClear = 1'b0;
    if(io_flush) begin
      logic_popPtr_willClear = 1'b1;
    end
  end

  assign logic_popPtr_willOverflowIfInc = (logic_popPtr_value == 2'b10);
  assign logic_popPtr_willOverflow = (logic_popPtr_willOverflowIfInc && logic_popPtr_willIncrement);
  always @(*) begin
    if(logic_popPtr_willOverflow) begin
      logic_popPtr_valueNext = 2'b00;
    end else begin
      logic_popPtr_valueNext = (logic_popPtr_value + _zz_logic_popPtr_valueNext);
    end
    if(logic_popPtr_willClear) begin
      logic_popPtr_valueNext = 2'b00;
    end
  end

  assign logic_ptrMatch = (logic_pushPtr_value == logic_popPtr_value);
  assign logic_pushing = (io_push_valid && io_push_ready);
  assign logic_popping = (io_pop_valid && io_pop_ready);
  assign logic_empty = (logic_ptrMatch && (! logic_risingOccupancy));
  assign logic_full = (logic_ptrMatch && logic_risingOccupancy);
  assign io_push_ready = (! logic_full);
  assign io_pop_valid = ((! logic_empty) && (! (_zz_io_pop_valid && (! logic_full))));
  assign io_pop_payload = _zz_logic_ram_port0;
  assign when_Stream_l1021 = (logic_pushing != logic_popping);
  assign logic_ptrDif = (logic_pushPtr_value - logic_popPtr_value);
  always @(*) begin
    if(logic_ptrMatch) begin
      io_occupancy = (logic_risingOccupancy ? 2'b11 : 2'b00);
    end else begin
      io_occupancy = ((logic_popPtr_value < logic_pushPtr_value) ? logic_ptrDif : _zz_io_occupancy);
    end
  end

  always @(*) begin
    if(logic_ptrMatch) begin
      io_availability = (logic_risingOccupancy ? 2'b00 : 2'b11);
    end else begin
      io_availability = ((logic_popPtr_value < logic_pushPtr_value) ? _zz_io_availability : _zz_io_availability_2);
    end
  end

  always @(posedge oscout or posedge reset) begin
    if(reset) begin
      logic_pushPtr_value <= 2'b00;
      logic_popPtr_value <= 2'b00;
      logic_risingOccupancy <= 1'b0;
      _zz_io_pop_valid <= 1'b0;
    end else begin
      logic_pushPtr_value <= logic_pushPtr_valueNext;
      logic_popPtr_value <= logic_popPtr_valueNext;
      _zz_io_pop_valid <= (logic_popPtr_valueNext == logic_pushPtr_value);
      if(when_Stream_l1021) begin
        logic_risingOccupancy <= logic_pushing;
      end
      if(io_flush) begin
        logic_risingOccupancy <= 1'b0;
      end
    end
  end


endmodule

module SpiSlaveCtrl (
  input               io_kind_cpol,
  input               io_kind_cpha,
  output              io_rx_valid,
  output     [7:0]    io_rx_payload,
  input               io_tx_valid,
  output              io_tx_ready,
  input      [7:0]    io_tx_payload,
  output              io_txError,
  output              io_ssFilted,
  input               io_spi_sclk,
  input               io_spi_mosi,
  output              io_spi_miso_write,
  output              io_spi_miso_writeEnable,
  input               io_spi_ss,
  input               oscout,
  input               reset
);

  wire                io_spi_sclk_buffercc_io_dataOut;
  wire                io_spi_ss_buffercc_io_dataOut;
  wire                io_spi_mosi_buffercc_io_dataOut;
  wire       [3:0]    _zz_counter_valueNext;
  wire       [0:0]    _zz_counter_valueNext_1;
  wire       [8:0]    _zz_buffer_1;
  wire       [2:0]    _zz_rspBit;
  wire       [2:0]    _zz_rspBit_1;
  wire                spi_sclk;
  wire                spi_mosi;
  wire                spi_miso_write;
  wire                spi_miso_writeEnable;
  wire                spi_ss;
  wire                _zz_normalizedSclkEdges_rise;
  wire                normalizedSclkEdges_rise;
  wire                normalizedSclkEdges_fall;
  wire                normalizedSclkEdges_toggle;
  reg                 _zz_normalizedSclkEdges_rise_1;
  reg                 counter_willIncrement;
  reg                 counter_willClear;
  reg        [3:0]    counter_valueNext;
  reg        [3:0]    counter_value;
  wire                counter_willOverflowIfInc;
  wire                counter_willOverflow;
  reg        [7:0]    buffer_1;
  reg                 counter_willOverflow_regNext;
  wire                rspBit;
  reg                 rspBitSampled;

  assign _zz_counter_valueNext_1 = counter_willIncrement;
  assign _zz_counter_valueNext = {3'd0, _zz_counter_valueNext_1};
  assign _zz_buffer_1 = {buffer_1,spi_mosi};
  assign _zz_rspBit = (3'b111 - _zz_rspBit_1);
  assign _zz_rspBit_1 = (counter_value >>> 1);
  BufferCC io_spi_sclk_buffercc (
    .io_dataIn  (io_spi_sclk                    ), //i
    .io_dataOut (io_spi_sclk_buffercc_io_dataOut), //o
    .oscout     (oscout                         ), //i
    .reset      (reset                          )  //i
  );
  BufferCC io_spi_ss_buffercc (
    .io_dataIn  (io_spi_ss                    ), //i
    .io_dataOut (io_spi_ss_buffercc_io_dataOut), //o
    .oscout     (oscout                       ), //i
    .reset      (reset                        )  //i
  );
  BufferCC io_spi_mosi_buffercc (
    .io_dataIn  (io_spi_mosi                    ), //i
    .io_dataOut (io_spi_mosi_buffercc_io_dataOut), //o
    .oscout     (oscout                         ), //i
    .reset      (reset                          )  //i
  );
  assign spi_sclk = io_spi_sclk_buffercc_io_dataOut;
  assign spi_ss = io_spi_ss_buffercc_io_dataOut;
  assign spi_mosi = io_spi_mosi_buffercc_io_dataOut;
  assign io_spi_miso_write = spi_miso_write;
  assign io_spi_miso_writeEnable = spi_miso_writeEnable;
  assign _zz_normalizedSclkEdges_rise = ((spi_sclk ^ io_kind_cpol) ^ io_kind_cpha);
  assign normalizedSclkEdges_rise = ((! _zz_normalizedSclkEdges_rise_1) && _zz_normalizedSclkEdges_rise);
  assign normalizedSclkEdges_fall = (_zz_normalizedSclkEdges_rise_1 && (! _zz_normalizedSclkEdges_rise));
  assign normalizedSclkEdges_toggle = (_zz_normalizedSclkEdges_rise_1 != _zz_normalizedSclkEdges_rise);
  always @(*) begin
    counter_willIncrement = 1'b0;
    if(!spi_ss) begin
      if(normalizedSclkEdges_toggle) begin
        counter_willIncrement = 1'b1;
      end
    end
  end

  always @(*) begin
    counter_willClear = 1'b0;
    if(spi_ss) begin
      counter_willClear = 1'b1;
    end
  end

  assign counter_willOverflowIfInc = (counter_value == 4'b1111);
  assign counter_willOverflow = (counter_willOverflowIfInc && counter_willIncrement);
  always @(*) begin
    counter_valueNext = (counter_value + _zz_counter_valueNext);
    if(counter_willClear) begin
      counter_valueNext = 4'b0000;
    end
  end

  assign io_ssFilted = spi_ss;
  assign io_rx_valid = counter_willOverflow_regNext;
  assign io_rx_payload = buffer_1;
  assign io_tx_ready = (counter_willOverflow || spi_ss);
  assign io_txError = (io_tx_ready && (! io_tx_valid));
  assign rspBit = io_tx_payload[_zz_rspBit];
  assign spi_miso_writeEnable = (! spi_ss);
  assign spi_miso_write = (io_kind_cpha ? rspBitSampled : rspBit);
  always @(posedge oscout) begin
    _zz_normalizedSclkEdges_rise_1 <= _zz_normalizedSclkEdges_rise;
    if(!spi_ss) begin
      if(normalizedSclkEdges_rise) begin
        buffer_1 <= _zz_buffer_1[7:0];
      end
    end
    counter_willOverflow_regNext <= counter_willOverflow;
    if(normalizedSclkEdges_fall) begin
      rspBitSampled <= rspBit;
    end
  end

  always @(posedge oscout or posedge reset) begin
    if(reset) begin
      counter_value <= 4'b0000;
    end else begin
      counter_value <= counter_valueNext;
    end
  end


endmodule

//BufferCC replaced by BufferCC

//BufferCC replaced by BufferCC

//BufferCC replaced by BufferCC

//BufferCC replaced by BufferCC

//BufferCC replaced by BufferCC

module BufferCC (
  input               io_dataIn,
  output              io_dataOut,
  input               oscout,
  input               reset
);

  (* async_reg = "true" *) reg                 buffers_0;
  (* async_reg = "true" *) reg                 buffers_1;

  assign io_dataOut = buffers_1;
  always @(posedge oscout) begin
    buffers_0 <= io_dataIn;
    buffers_1 <= buffers_0;
  end


endmodule
