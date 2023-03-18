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
    val being_read : State = new State
    val being_written : State = new State
    val start_transfer : State = new State

    idle.whenIsActive {
      new Sequencer()

        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.status, SpiSlaveCtrlInt.ssEnabledIntEnable))
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.config,0x00))
        .addStep(interrupt)
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data,B(0xff).resize(32)))
        // fill the tx payload, we need it to trigger txInt normally
        .addStep(apb_operation.write_t(spi_slave_regs.status, SpiSlaveCtrlInt.ssEnabledIntClear){
          goto(start_transfer)
        })
    }


    start_transfer.whenIsActive {
      val is_written = RegInit(False)
      new Sequencer()
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.status, SpiSlaveCtrlInt.txIntEnable | SpiSlaveCtrlInt.rxListen))
        // use txInterrupt, because txInt would be asserted before rxInt, so we have more time to prepare data
        // the only thing we need to do for txInt is filling tx payload after SS enable
        .addStep(interrupt)
        .addStep(apb_operation.read_t(spi_slave_regs.data){
          rdata =>{
            reg_addr := rdata(7 downto 1).asUInt
            is_written := rdata.lsb
            //when(rdata(0))(goto(being_written)).otherwise(goto(being_read))
          }})
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data,regs(reg_addr).takeHigh(8).resize(32)))
        .addStep(apb_operation.write_t(spi_slave_regs.data,regs(reg_addr).takeLow(8).resize(32)){
          when(is_written)(goto(being_written)).otherwise(goto(idle))
        })
    }

    being_read.whenIsActive{
      //spislave_fifo_write(regs(reg_addr),2){ // we push 2 bytes into fifo, then go for rest
       // goto(idle)
      //}

      new Sequencer()
        .addStep(apb_operation.write_t_withoutcallback(spi_slave_regs.data,regs(reg_addr).takeHigh(8).resize(32)))
        .addStep(apb_operation.write_t(spi_slave_regs.data,regs(reg_addr).takeLow(8).resize(32)){
          goto(idle)
        })
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
}


object SPI_PWM_TEST {
  def main(args: Array[String]) {
    //InOutWrapper
    SpinalVerilog(new SPI_PWM_Top).printPruned()
  }
}