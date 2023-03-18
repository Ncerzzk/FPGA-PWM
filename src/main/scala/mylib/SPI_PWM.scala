package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb.{Apb3, Apb3Config, Apb3SlaveFactory}
import spinal.lib.com.spi.{Apb3SpiSlaveCtrl, SpiSlave, SpiSlaveCtrlGenerics, SpiSlaveCtrlMemoryMappedConfig}
import spinal.lib.fsm.{EntryPoint, State, StateMachine}

object APB3Phase extends SpinalEnum{
  val IDLE, SETUP, ACCESS = newElement
}

object SpiSlaveCtrlInt{
  val txIntEnable=0x01
  val rxIntEnable=0x02
  val ssEnabledIntEnable=0x04
  val ssDisabledIntEnable=0x08

  val ssEnabledIntClear=1<<12
  val ssDisabledIntClear=1<<13

  val rxListen = 1<<15

}

class APB3OperationArea(apb_m:Apb3) extends Area{
  import APB3Phase._
  val phase = RegInit(SETUP) //optimization for faster apb access
  val transfer = False
  var writeOkMap=Map[(Component,UInt),Bool]()
  apb_m.PADDR.setAsReg()
  apb_m.PWRITE.setAsReg()

  apb_m.PENABLE := False
  apb_m.PWDATA := B(0)
  apb_m.PWRITE.rise()
  def write_t(addr:UInt,value:Bits)(block: => Unit = Unit):Bool={
    apb_m.PWRITE := True
    apb_m.PADDR := addr.resized
    apb_m.PWDATA := value
    transfer := True

    when(apb_m.PENABLE){
      transfer := False
      block
    }
//    val key = (Component.current ,addr)
//    if(writeOkMap.contains(key)){
//      return writeOkMap(key)
//    }

    //val ret = apb_m.PENABLE & apb_m.PWRITE & apb_m.PADDR===addr.resized
    val ret = apb_m.PENABLE
    //writeOkMap += (Component.current,addr)->ret
    ret
  }

  def write_t_withoutcallback(addr:UInt,value:Bits):Bool={
    write_t(addr,value){}
  }
  def write(addr:UInt,value:Bits)(block: => Unit = Unit): Unit ={
    // we should use block :=> Unit but not block: Unit here,
    // so that the block would be called inside the correct position
    apb_m.PWRITE := True
    apb_m.PADDR := addr.resized
    apb_m.PWDATA := value
    transfer := True

    when(apb_m.PENABLE){
      transfer := False
      block
    }
  }
  def read_t(addr:UInt)(block: Bits => Unit) ={
    apb_m.PWRITE := False
    apb_m.PADDR := addr.resized
    transfer := True
    when(apb_m.PENABLE){
      transfer := False
      block(apb_m.PRDATA)
    }
    apb_m.PENABLE
  }
  def read(addr:UInt)(block: => Unit = {}): Unit ={
    apb_m.PWRITE := False
    apb_m.PADDR := addr.resized
    transfer := True
    when(apb_m.PENABLE){
      transfer := False
      block
    }
  }

  switch(phase){
    is(IDLE){
      apb_m.PSEL:= B(0)
      apb_m.PENABLE := False
      when(transfer){
        phase := SETUP
      }
    }

    is(SETUP){
      apb_m.PSEL:=B(1)
      when(transfer){
        phase:= ACCESS
      }
    }

    is(ACCESS){
      apb_m.PSEL:=B(1)
      apb_m.PENABLE := True
      phase:= SETUP
    }
  }
}
class SPI_PWM extends Component{
  import utils._
  val apb_m = master(Apb3(Apb3SpiSlaveCtrl.getApb3Config))
  val interrupt = in Bool()
  val sclk = in Bool()  // used to count sclk by ourself
  val mosi = in Bool()  // used to handle the first byte by ourself

  val sclk_sync = BufferCC(sclk)
  val mosi_sync = BufferCC(mosi)

  val spi_slave_regs= new Area{
    val data = U("32'b0")
    val status = U("32'h4")
    val config = U("32'h8")
  }

  val regs = new RegMem(16 bits,16)
  regs.add(0x00,"period","period of the counter")
  regs.add(0x01,"CCR1",desc="ccr of channel 1")
  regs.add(0x02,"CCR2",desc="ccr of channel 2")

  val apb_operation= new APB3OperationArea(apb_m)

  val fsm = new StateMachine {
    val reg_addr = Reg(UInt(7 bits)).init(0)
    val idle : State = new State with EntryPoint
    val being_written : State = new State
    val start_transfer : State = new State

    val sclk_count = Counter(8)
    val sclk_cnt_start = RegInit(False)
    val temp_rx = Reg(Bits(8 bits)).init(0)

    idle.whenIsActive {
      new Sequencer()
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.status, SpiSlaveCtrlInt.ssEnabledIntEnable))
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.config,0x00))
        .addStep(interrupt)
        .addStep{
          goto(start_transfer)
          True
        }

    }

    start_transfer.onEntry{
      sclk_cnt_start:=True
      temp_rx:=B(0).resized
    }

    start_transfer.onExit{
      sclk_cnt_start:=False
      sclk_count.clear()
    }

    when(sclk_cnt_start && sclk_sync.rise()){
      sclk_count.increment()
      temp_rx := (temp_rx ## mosi_sync).resized
      // only work at cpol 0, cpha 0
      // handle the first rx byte by ourself, to make the module could run at high sclk freq
    }
    start_transfer.whenIsActive {

      new Sequencer()
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.status, SpiSlaveCtrlInt.ssEnabledIntClear))
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data, 0xff))  // write anything to tx payload to send in the first byte
        .addStep(sclk_count.value === U(7))
        .addStep{
          reg_addr := temp_rx.takeLow(7).asUInt
          True
        }
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data,regs(reg_addr).takeHigh(8).resize(32)))
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data,regs(reg_addr).takeLow(8).resize(32)))
        .addStep(sclk_count.value===0)
        .addStep{
           when(temp_rx.lsb)(goto(being_written)).otherwise(goto(idle))
           True
        }
    }

    being_written.whenIsActive{
      val data = Reg(Bits(16 bits))
      new Sequencer()
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.status, SpiSlaveCtrlInt.ssDisabledIntEnable | SpiSlaveCtrlInt.rxListen))
        .addStep(interrupt)
        .addStep(apb_operation.read_t(spi_slave_regs.data){
          rdata => data.takeHigh(8) := rdata.takeLow(8)
        })
        .addStep(apb_operation.read_t(spi_slave_regs.data){
          rdata => data.takeLow(8) := rdata.takeLow(8)
        })
        .addStep(apb_operation.write_t(spi_slave_regs.status, SpiSlaveCtrlInt.ssDisabledIntClear){
          regs(reg_addr) := data
          goto(idle)
        })

    }
  }

}

class SPI_PWM_Top extends Component{
  val spi_pins = master(SpiSlave())
  val spi_pwm = new SPI_PWM
  val spi_slave_ctrl = Apb3SpiSlaveCtrl(SpiSlaveCtrlMemoryMappedConfig(SpiSlaveCtrlGenerics()))
  spi_pwm.interrupt := spi_slave_ctrl.io.interrupt
  spi_pwm.apb_m <> spi_slave_ctrl.io.apb
  spi_slave_ctrl.io.spi <> spi_pins
  spi_pwm.sclk := spi_pins.sclk
  spi_pwm.mosi := spi_pins.mosi
}


object SPI_PWM_TEST {
  def main(args: Array[String]) {
    //InOutWrapper
    SpinalVerilog(new SPI_PWM_Top).printPruned()
  }
}