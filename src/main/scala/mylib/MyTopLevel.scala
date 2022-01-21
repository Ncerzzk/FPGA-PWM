/*
 * SpinalHDL
 * Copyright (c) Dolu, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb._
import spinal.lib.com.i2c._
import spinal.lib.fsm._



object I2CRegs{
  val hitContext=0x84
  val rxAck=0x0C
  val txData=0x00
  val txAck=0x04
  val rxData=0x08
  val interruptConfig=0x20


  object interrupt{
    val rxDataEnable=0x01
    val rxAckEnable=1<<1
    val txDataEnable=1<<2
    val txAckEnable=1<<3

    val endEnable=1<<6
    val dropEnable=1<<7
  }

}

object APB_Operator extends SpinalEnum {
  val FSM, INT = newElement()
}

object Seq_Area{
  def apply(seqs:Seq[(=>Unit) => WhenContext],state:UInt)= new Area{
    switch(state){
      for(i <- seqs.indices){
        is(i){
          seqs(i){
            state := i+1
          }
        }
      }
    }
  }

  def apply(seqs:Seq[(=>Unit) => WhenContext]):Area={
    val state =Reg(UInt(log2Up(seqs.length+1) bits)).init(0)
    apply(seqs,state)
  }
}

class PWM extends Component{
  val apb = master(Apb3(Apb3I2cCtrl.getApb3Config))
  val int = in Bool()
  val pwm_out= new Bundle{
    val ch1 = out Bool()
    //val ch2 = out Bool()
    //val ch3 = out Bool()
    //val ch4 = out Bool()
  }

  val pwm_area=new Area{

    val counter = Reg(UInt(16 bits)).init(0)
    val period = Reg(UInt(16 bits)).init(2000)
    val ccr1 = Reg(UInt(16 bits)).init(1000)

    counter := counter+1
    when(counter === period){
      counter := 0
    }
    pwm_out.ch1 := counter < ccr1
  }

  def seq_execute(seqs:Seq[Unit => Object]) =new Area{
    val state=Reg(UInt(log2Up(seqs.length+1) bits)).init(0)
    switch(state){
      for(i <- seqs.indices){
        is(i){
          seqs(i){
            state := i+1
          }
        }
      }
    }
  }


  val apb_operate_area = new Area{
    val ok = apb.PENABLE
    val result = Reg(cloneOf(apb.PRDATA)).init(0)
    val state = RegInit(U"00")

    apb.PSEL := 1
    apb.PADDR := 0
    apb.PWDATA := 0
    apb.PWRITE := False
    apb.PENABLE := False

    switch(state){
      is(1){
        state :=2
      }
      is(2){
        state :=0
      }
    }
    apb.PENABLE.setWhen(state === 2)

    when(state===1 && !apb.PWRITE){
      result := apb.PRDATA
    }
  }

  def read(addr:UInt)(block: => Unit)={
    apb.PADDR := addr.resized
    apb.PWRITE := False
    when(apb_operate_area.state === 0){
      apb_operate_area.state := 1
    }
    when(apb_operate_area.ok){
      block
    }
  }
  def write(addr:UInt,data:Bits)(block: => Unit)={
    apb.PADDR := addr.resized
    apb.PWDATA := data.resized
    apb.PWRITE := True

    when(apb_operate_area.state === 0){
      apb_operate_area.state := 1
    }
    when(apb_operate_area.ok){
      block
    }
  }

  def write_regs(regs:List[(Int,Int)])(block: =>Unit) = new Area{

    val cnt=Reg(UInt(log2Up(regs.length+1) bits)).init(0)
    switch(cnt){
      for (i <- regs.indices){
        val reg=regs(i)._1
        val value=regs(i)._2
        is(i){
          write(reg,value){cnt := i+1}
        }
      }
      is(regs.length){
        block
      }
    }
  }

  val int_ctrl=new Area{
    val int_ctrl_state= RegInit(U"00")
    val common_int = Bool
    val end_flag = Bool

    when(int.rise(False)){
      int_ctrl_state := 1
    }

    end_flag := False
    common_int := False

    switch(int_ctrl_state){
      is(1){
        read(I2CRegs.interruptConfig){
          end_flag :=apb_operate_area.result(8 to 9).orR
          common_int := !end_flag
          int_ctrl_state := 0
        }
      }
    }
  }

  val init = new Area{
    val ok = RegInit(False)
    import I2CRegs.interrupt._
    val regs=List(
      (0x28,30),   //  clk divider =50
      (0x2c,0xFFFFF),  // timeout = 0xFFFFF (just to test)
      (0x30,2),   // tsu_data = 0x02 (just to test)
      (0x88,0x20| (1<<15)), // address0 = 0x20
      (I2CRegs.interruptConfig,txDataEnable | rxDataEnable | txAckEnable | dropEnable | endEnable )  //txData Enable  //rxData  //txAck
    )

    write_regs(regs){
      ok:=True
    }
  }

  val ctrl = new Area{
    def activate_tx_ack(block: =>Unit)=write(I2CRegs.txAck,0x03<<8)(block)
    def activate_tx_nack(block: =>Unit)=write(I2CRegs.txAck,0x03<<8|0x01)(block)
    def deactivate_tx_ack(block: =>Unit)=write(I2CRegs.txAck,0x00)(block)
    def listen_rx_data(block: =>Unit)=write(I2CRegs.rxData,1<<9)(block)
    def listen_rx_ack(block: =>Unit)=write(I2CRegs.rxAck,1<<9)(block)
    def ignore_rx_data(block: =>Unit)=write(I2CRegs.rxData,0)(block)
    def ignore_rx_ack(block: =>Unit)=write(I2CRegs.rxAck,0)(block)
    def tx_data(data:Bits)(block: =>Unit)=write(I2CRegs.txData,3<<8|data.resized)(block)

    def interrupt_set(config:Int)(block: =>Unit)={
      import I2CRegs.interrupt._
      write(I2CRegs.interruptConfig,endEnable | dropEnable | config)(block)
    }


    val reg_choose=new Area{
      val regs=List(0->pwm_area.period,1->pwm_area.ccr1)
      val addr = Reg(UInt(8 bits)).init(0)
      val read=addr.muxList(U(0),regs)

      def write(data:Bits): Unit ={
        switch(addr){
          for (i <- regs){
            is(i._1){
              i._2.assignFromBits(data)
            }
          }
          default{
            regs(0)._2.assignFromBits(data)
          }
        }
      }
    }

    val slave_drive=new Area{
      val active=RegInit(False)
      val state:UInt = RegInit(U"000")
      val data=Bits(8 bits)
      val finish = False

      data := 0
      def start(txdata:Bits)={
        when(!active){
          //state := 1
          active.set()
        }
        data := txdata
      }
      when(active){
        Seq_Area(List(
          interrupt_set(I2CRegs.interrupt.rxAckEnable),
          listen_rx_ack,
          tx_data(data),
          when(int_ctrl.common_int),
          deactivate_tx_ack,
          block=>{
            active := False
            finish := True
            when(True)(block)
          }
        ),
          state
        )
      }


    }

    val master_drive = new Area{
      val active=RegInit(False)
      val state: UInt = RegInit(U"000")
      val byte=Bits(8 bits)
      val finish=False.setWhen(active.fall(False))

      val need_ack=Bool()

      byte := apb_operate_area.result.resized

      need_ack:=True
      def start: WhenContext ={
        when(!active){
          state := 1
          active := True
        }
      }

      def start_without_ack ={
        when(!active){
          state := 1
          active := True
        }
        need_ack := False
      }

      when(active){
        switch(state){
          is(1) {
            listen_rx_data{state := 2}
          }
          is(2){
            // wait for master send byte
            when(int_ctrl.common_int)(state:=3)
          }
          is(3){
            when(need_ack){
              activate_tx_ack{state:=4} // send ack to master
            }otherwise{
              activate_tx_nack{state:=4}
            }
          }
          is(4){
            read(I2CRegs.rxData){
              state:=5
            }
          }
          is(5){
            active := False
          }
        }
      }
    }

    val fsm = new StateMachine{
      val init_state:State = new State with EntryPoint{
        whenIsActive{
          when(init.ok){
            goto(idle)
          }
        }
      }
      always{
        when(int_ctrl.end_flag){
          goto(idle)
        }
      }
      val idle:State = new State{

        val idle_state = RegInit(U"000")
        onEntry{
          idle_state := 0
        }
        whenIsActive{
          Seq_Area(
            List(
              block=>when(int_ctrl.common_int){
                block
              },
              activate_tx_ack _,
              block=>write(I2CRegs.interruptConfig,I2CRegs.interrupt.rxDataEnable){
                goto(hit)
                block
              }
            ),
            idle_state
          )
        }
      }

      val master_write:State = new State{
        val write_state=RegInit(U"00")
        val reg_temp=Reg(Bits(16 bits))
        onEntry{
          write_state :=0
        }
        whenIsActive{
          switch(write_state){
            is(0){
              master_drive.start
              when(master_drive.finish){
                write_state:=1
                reg_temp.takeHigh(8) := master_drive.byte
              }
            }
            is(1){
              master_drive.start_without_ack
              when(master_drive.finish){
                reg_temp.takeLow(8) :=master_drive.byte
                write_state :=2
              }
            }
            is(2){
              reg_choose.write(reg_temp)
              //pwm_area.period.assignFromBits(reg_temp)
              goto(idle)
            }
          }
        }
      }

      val master_read:State = new State{
        val read_state=RegInit(U"000")
        onEntry{
          read_state :=0
        }
        whenIsActive{
          switch(read_state){
            is(0){
              slave_drive.start(reg_choose.read.takeHigh(8))
              when(slave_drive.finish){
                read_state:=1
              }
            }
            is(2){
              when(int_ctrl.common_int)(read_state:=3)
            }

            is(3){
              slave_drive.start(reg_choose.read.takeLow(8))
            }

          }
        }
      }

      val hit:State =new State{
        val hit_state = RegInit(U"000")
        val hit_context = RegInit(False)    // 0 is write , 1 is read
        onEntry{
          hit_state :=  0
          hit_context := False
        }
        whenIsActive {
          switch(hit_state){
            is(0){
              read(I2CRegs.hitContext){
                hit_context := apb_operate_area.result.lsb
                hit_state :=1
              }
            }
            is(1){
              master_drive.start
              when(master_drive.finish){
                hit_state:=2
                reg_choose.addr := master_drive.byte.asUInt
              }
            }
            is(2){
              when(!hit_context){
                goto(master_write)
              }otherwise{
                goto(master_read)
              }

            }
          }

        }
      }

    }
  }
}

//Hardware definition
class MyTopLevel extends Component {

  val i2c = master(I2c())
  val i2c_apb=new Apb3I2cCtrl(
    I2cSlaveMemoryMappedGenerics(
      ctrlGenerics = I2cSlaveGenerics(
        samplingWindowSize = 3,
        samplingClockDividerWidth = 10 bits,
        timeoutWidth = 20 bits
      ),
      addressFilterCount = 1
    )
  )
  val a = new PWM()
  a.apb <> i2c_apb.io.apb


  a.int := i2c_apb.io.interrupt
  i2c <> i2c_apb.io.i2c

}

//Generate the MyTopLevel's Verilog
object MyTopLevelVerilog {
  def main(args: Array[String]) {
    //InOutWrapper
    SpinalVerilog(new PWM).printPruned()
  }
}

//Generate the MyTopLevel's VHDL
object MyTopLevelVhdl {
  def main(args: Array[String]) {
    SpinalVhdl(new MyTopLevel)
  }
}


//Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one. This configuration can be resued everywhere
object MySpinalConfig extends SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC))

//Generate the MyTopLevel's Verilog using the above custom configuration.
object MyTopLevelVerilogWithCustomConfig {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(new MyTopLevel)
  }
}