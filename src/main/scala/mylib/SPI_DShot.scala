package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb.{Apb3, Apb3Config, Apb3SlaveFactory}
import spinal.lib.com.spi.{Apb3SpiSlaveCtrl, SpiSlave, SpiSlaveCtrlGenerics, SpiSlaveCtrlMemoryMappedConfig}
import spinal.lib.fsm.{EntryPoint, State, StateFsm, StateMachine}
import spinal.lib.io.InOutWrapper

class SPI_DShot_Top(forfpga:Boolean = false) extends Component{
  val clkin = in Bool()
  val spi_pins = master(SpiSlave())

  val pll = forfpga.generate(new Gowin_rPLL)
  pll.io.clkin := clkin
  // val osc = forfpga.generate(new Gowin_OSC)

  var myClockDomain = ClockDomain.current
  if(forfpga) {
    myClockDomain = ClockDomain(pll.io.clkout,clockDomain.reset)
  }

  val spi_dshot =myClockDomain{
    new SPI_DShot(8)
  }
  val spi_slave_ctrl =myClockDomain{
    Apb3SpiSlaveCtrl(SpiSlaveCtrlMemoryMappedConfig(SpiSlaveCtrlGenerics(),3,3))
  }

  val d_out  =  out( Bits(8 bits))
  d_out := spi_dshot.dshot.d_out

  spi_dshot.interrupt := spi_slave_ctrl.io.interrupt
  spi_dshot.apb_m <> spi_slave_ctrl.io.apb
  spi_slave_ctrl.io.spi <> spi_pins
  spi_dshot.sclk := spi_pins.sclk
  spi_dshot.mosi := spi_pins.mosi
  spi_dshot.ss := spi_pins.ss

}


object SPI_DShot_Gen_For_FPGA {
  def main(args: Array[String]) {
    //InOutWrapper
    SpinalVerilog(InOutWrapper(new SPI_DShot_Top(true))).printPruned()
  }
}

class SPI_DShot(channel_num:Int) extends Component{
  import utils._
  val apb_m = master(Apb3(Apb3SpiSlaveCtrl.getApb3Config))
  val interrupt = in Bool()
  val sclk = in Bool()  // used to count sclk by ourself
  val mosi = in Bool()  // used to handle the first byte by ourself
  val ss = in Bool()  // used to handle ss by ourself

  val sclk_sync = BufferCC(sclk)
  val mosi_sync = BufferCC(mosi)
  val ss_sync = BufferCC(ss,init = True)

  val spi_slave_regs= new Area{
    val data = U("32'b0")
    val status = U("32'h4")
    val config = U("32'h8")
  }

  val regs = new RegMem(16 bits,16)
  val dshot = new DShot(regs,channel_num)

  val apb_operation= new APB3OperationArea(apb_m)

  val spi_fsm = new StateMachine {
    val reg_addr = Reg(UInt(7 bits)).init(0)
    val idle : State = new State with EntryPoint
    val being_written  = new StateFsm(internalBeingWrittenFSM()){
      whenCompleted(goto(idle))
    }
    val start_transfer : State = new State

    val sclk_count = Counter(8)
    val sclk_cnt_start = RegInit(False)
    val temp_rx = Reg(Bits(8 bits)).init(0)
    val sclk_rise= sclk_sync.rise()

    val ss_has_fallen = RegInit(False)

    always{
      when(ss_sync && !isActive(being_written)){  //handle ss -> high situation
        goto(idle)
      }

      when(ss_sync.fall()){
        ss_has_fallen := True
        temp_rx := B(0)
      }

      /*
      Previously, we started receiving temp_rx only after entering start_transfer.
      However, in some cases where there are consecutive SPI read/write operations
      (i.e., short duration of SS high level), it is possible that two or three
      SCLK pulses have already passed by the time we reach start_transfer.
      Therefore, we need to start receiving temp_rx as soon as SS goes low.
       */
      when((ss_has_fallen || isActive(start_transfer)) && sclk_rise){
        sclk_count.increment()
        temp_rx := (temp_rx ## mosi_sync).resized
      }


    }

    idle.whenIsActive {
      new Sequencer()
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.config,0x00)) // set cpol and cpha
        .addStep {
          ss_has_fallen := False
          ss_has_fallen
        }
        .addStep{
          goto(start_transfer)
          True
        }

    }

    start_transfer.onExit{
      sclk_count.clear()
    }
    val readwrite_bit = RegInit(False)
    start_transfer.whenIsActive {
      val reg_data = Reg(Bits(16 bits)).init(0).dontSimplifyIt()
      new Sequencer()
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.status, SpiSlaveCtrlInt.ssEnabledIntClear))
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data, 0xff))  // write anything to tx payload to send in the first byte
        .addStep(sclk_count.value === U(7))
        .addStep{
          reg_addr := temp_rx.takeLow(7).asUInt
          reg_data := regs(temp_rx.takeLow(7).asUInt.resized)
          True
        }
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data,reg_data.takeHigh(8).resize(32)))
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data,reg_data.takeLow(8).resize(32)))
        .addStep{
          readwrite_bit := temp_rx.lsb   // save read write bit here, because we will wait one more sclk to jump to next state(being written or idle)
          sclk_count.value === U(0)}
        .addStep(sclk_rise)   // wait one more sclk, to fix wrong interrupt assert in slow sclk case
        .addStep{
          when(readwrite_bit)(goto(being_written)).otherwise(goto(idle))
          True
        }
    }


    def internalBeingWrittenFSM()=new StateMachine{
      val data = Reg(Bits(8 bits)).init(0)
      val ptr = Reg(cloneOf(reg_addr)).init(0)
      val is_high_8bit = RegInit(True)
      val ss_has_rised = RegInit(False)
      val temp_data = Reg(Bits(16 bits)).init(0)

      always{
        when(ss_sync.rise()){
          ss_has_rised := True
        }
      }

      val init:State = new State with EntryPoint{
        whenIsActive {
          is_high_8bit := True   // init the flag to high to fix the misaligned registers issue after a corrupt write sequence
          ss_has_rised := False
          apb_operation.write_t(spi_slave_regs.status,
            SpiSlaveCtrlInt.rxIntEnable  | SpiSlaveCtrlInt.rxListen) {
            ptr := reg_addr
            goto(wait_s)
          }
        }
      }

      val wait_s:State = new State{
        whenIsActive{
          when(interrupt)(goto(read)).otherwise{
            when(ss_has_rised){
              exitFsm()
            }
          }

          when(is_high_8bit.rise()) {
            ptr := ptr + 1
            regs(ptr) := temp_data

            when(ptr > 0 && ptr <= channel_num){
              val channel_index = ptr - 1
              dshot.trigers(channel_index.takeLow(3).asUInt) := True
            }
          }
        }
      }

      val read:State = new State{

        whenIsActive{
          apb_operation.read_t(spi_slave_regs.data){
            rdata => {
              val rdata_low = rdata.takeLow(8)
              is_high_8bit := !is_high_8bit
              temp_data := data ## rdata_low
              data := rdata_low
              goto(wait_s)
            }
          }
        }
      }
    }


  }

}

