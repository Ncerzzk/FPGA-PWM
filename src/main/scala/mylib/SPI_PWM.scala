package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb.{Apb3, Apb3Config, Apb3SlaveFactory}
import spinal.lib.com.spi.{Apb3SpiSlaveCtrl, SpiSlave, SpiSlaveCtrlGenerics, SpiSlaveCtrlMemoryMappedConfig}
import spinal.lib.fsm.{EntryPoint, State, StateMachine}

object APB3Phase extends SpinalEnum{
  val IDLE, SETUP, ACCESS = newElement
}

class APB3OperationArea(apb_m:Apb3) extends Area{
  import APB3Phase._
  val phase = RegInit(IDLE)
  val transfer = False
  apb_m.PADDR.setAsReg()
  apb_m.PWRITE.setAsReg()

  apb_m.PENABLE := False
  apb_m.PWDATA := B(0)
  apb_m.PWRITE.rise()
  def write(addr:UInt,value:Bits)(block: => Unit = Unit): Unit ={
    // we should use block :=> Unit but not block: Unit here,
    // so that the block would be called inside the correct position
    apb_m.PWRITE := True
    apb_m.PADDR := addr
    apb_m.PWDATA := value
    transfer := True

    when(apb_m.PENABLE){
      transfer := False
      block
    }
  }

  def read(addr:UInt)(block: => Unit = {}): Unit ={
    apb_m.PWRITE := False
    apb_m.PADDR := addr
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
      phase:= ACCESS
    }

    is(ACCESS){
      apb_m.PSEL:=B(1)
      apb_m.PENABLE := True
      when(transfer){
        phase := SETUP
      }otherwise{
        phase := IDLE
      }
    }
  }
}
class SPI_PWM extends Component {
  val apb_m = master(Apb3(Apb3SpiSlaveCtrl.getApb3Config))
  val interrupt = in Bool()
  val spi_lines = slave(SpiSlave())

  val inited = Reg(Bool).init(False)

  val regs = new Regs
  regs.add("period",0,2,"period of the counter")
  regs.add("CCR1",1,2,desc="ccr of channel 1")

  val reg_area = regs.buildArea()

  val apb_operation= new APB3OperationArea(apb_m)

  val test=Bits(16 bits)
  test:=0
  when(!inited){
    apb_operation.write(0x4,0xF){
      inited := True
      test:=reg_area.read(0x111)
    }
  }

  val interrupt_status = Reg(Bits(4 bits)).init(0)
  val interrupt_status_valid = RegInit(False)
  when(interrupt & !interrupt_status_valid){
    apb_operation.read(0x04){
      interrupt_status := apb_m.PRDATA(11 downto 8)
      interrupt_status_valid := True
    }
  }
  val fsm = new StateMachine {
    val reg_addr = Reg(UInt(7 bits)).init(0)
    val idle : State = new State with EntryPoint
    val being_read : State = new State
    val being_written : State = new State
    val start_transfer : State = new State
    idle.onEntry{
      interrupt_status := 0
      interrupt_status_valid := False
    }
    idle.whenIsActive{
      when(interrupt_status_valid && interrupt_status(2)){
        apb_operation.write(0x4,1<<12 | 1<<13){
          // write bit 12 of status reg to clear the enable interrupt
          // write bit 13 of status reg to clear potential disable interrupt
          goto(start_transfer)
        }
      }
    }

    start_transfer.whenIsActive{
      when(interrupt_status_valid && interrupt_status(1)){
        apb_operation.read(0x00){
          reg_addr := apb_m.PRDATA(7 downto 1).asUInt
          when(apb_m.PRDATA(0)){  // 0 is read, 1 is write
            goto(being_written)
          }otherwise{
            goto(being_read)
          }
        }
      }
    }

    def spislave_fifo_write_byte(value :Bits)(block: =>Unit): Unit ={
      apb_operation.write(0x00,value.resize(32)){block}
    }

    def spislave_fifo_write(value:Bits, size:Int)(block: =>Unit) : Unit ={
      val cnt= Reg(UInt(log2Up(size) bits)).init(0)
      switch(cnt){
        for(i <- 0 until size){
          is(i) {
            spislave_fifo_write_byte(value >> (size-i-1) * 8) {
              if(i==size-1){
                block
              }else {
                cnt := cnt + 1
              }
            }
          }
        }
        if(size != 1<<cnt.getBitsWidth) {
          default {
            spislave_fifo_write_byte(0x0) {}
          }
        }
      }
    }

    val data_pushad=RegInit(False)
    being_read.onEntry{
      data_pushad := False
    }
    being_read.whenIsActive{
      when(!data_pushad){
        spislave_fifo_write(reg_area.read(reg_addr),2){
          data_pushad := True
        }
      }elsewhen(interrupt_status_valid && interrupt_status(0)){

      }
    }
  }

}

class SPI_PWM_Top extends Component{
  val spi_pwm = new SPI_PWM
  val spi_slave_ctrl = Apb3SpiSlaveCtrl(SpiSlaveCtrlMemoryMappedConfig(SpiSlaveCtrlGenerics()))

  spi_pwm.interrupt := spi_slave_ctrl.io.interrupt
  spi_pwm.apb_m <> spi_slave_ctrl.io.apb
  spi_pwm.spi_lines <> spi_slave_ctrl.io.spi
}


object SPI_PWM_TEST {
  def main(args: Array[String]) {
    //InOutWrapper
    SpinalVerilog(new SPI_PWM_Top).printPruned()
  }
}