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

import mylib.I2CRegs.interrupt._
import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb._
import spinal.lib.com.i2c._
import spinal.lib.fsm._
import spinal.lib.io.InOutWrapper

import scala.collection.immutable
import scala.collection.mutable.ListBuffer



object I2CRegs{
  val hitContext=0x84
  val rxAck=0x0C
  val txData=0x00
  val txAck=0x04
  val rxData=0x08
  val interruptConfig=0x20
  val interruptFlag=0x24
  val samplingClockDivider=0x28
  val timeout=0x2C
  val tsuDat=0x30
  val filteringConfig_0=0x88
  val hitOrNot=0x80


  object interrupt{
    val rxDataEnable=0x01
    val rxAckEnable=1<<1
    val txDataEnable=1<<2
    val txAckEnable=1<<3

    val endEnable=1<<6
    val dropEnable=1<<7
    val startEnable=1<<4
    val restartEnable=1<<5
  }

}

object Seq_Area{
  def apply(seqs:Seq[(=>Unit) => Unit],state:UInt): Unit ={
    seq_switch(seqs,state)
  }

  def seq_switch(seqs:Seq[(=>Unit) => Unit],state:UInt)={
    switch(state){
      for(i <- seqs.indices){
        is(i){
          seqs(i){
            if(i+1!=seqs.length){
              state := i+1
            }else{
              state := 0
            }
          }
        }
      }
    }
  }

  def apply(seqs:Seq[(=>Unit) => Unit]):Unit={
    val state =Reg(UInt(log2Up(seqs.length+1) bits)).init(0).keep()
    seq_switch(seqs,state)
  }
}

class SubPWM(enable:Bool) extends Area{
  val period = Reg(UInt(16 bits)).init(2000)
  val counter = Reg(UInt(16 bits)).init(0)

  val period_buf=RegNext(period)

  def count():Unit= {
    when(counter === period || period_buf =/= period){
      counter := 0
    }elsewhen(enable) {
      counter := counter + 1
    }
  }

}


class PWM(channel_num:Int=4, sub_PWM_num:Int=2) extends Component{
  assert(sub_PWM_num>=1)

  val apb = master(Apb3(Apb3I2cCtrl.getApb3Config))
  val int = in Bool()
  val pwm_out= new Bundle{
    for(i<-0 until channel_num){
      valCallback(out Bool(),"ch"+(i+1).toString)
    }
  }

  val config_reg= Reg(UInt(16 bits)).init(0)

  val sub_pwms=List(new SubPWM(True),new SubPWM(config_reg(14)))

  class PWMChannel(config:Bits) extends Area{
    val output=Bool
    val ccr=Reg(UInt(16 bits)).init(0)
    val counter_map=config.muxList(U(0),for(index <- 0 until sub_PWM_num) yield (index,sub_pwms(index).counter))

    output := counter_map<ccr
  }



  val pre_divicder=new Area{
    val counter=Reg(UInt(5 bits)).init(0)
    val divider_val= config_reg.takeLow(5).asUInt
    val clk_en = False.setWhen(counter===divider_val)

    counter := counter+1
    when(counter === divider_val ){
      counter :=0
    }
  }

  val pwm_area=new Area{

    val ccrmap_regs=List.fill((channel_num/4+0.5).toInt)(Reg(UInt(16 bits)).init(0))
    val channels = for(i <- 0 until channel_num)  yield {
      val range = (3 + i%4 *4) downto (0 + i%4 *4)
      new PWMChannel(ccrmap_regs(i / 4)(range).asBits)
    }

    val counter = sub_pwms(0).counter
    val period = sub_pwms(0).period

    val output_active= channels.map(x=>x.ccr.asBits.orR).reduce((a,b)=>a|b)


    val timeout_area=new Area{
      val cnt_max= Vec(Reg(UInt(16 bits)).init(0), Reg(UInt(16 bits)).init(0))
      val counter = Reg(UInt(32 bits))
      val enable = config_reg.msb
      val flag= Reg(Bool()).init(False)


      when(enable && output_active && !flag){
        counter:= counter +1
      }

      when(counter === cnt_max.asBits.asUInt){
        flag := True  // the counter would stop at cnt_max +1 , but it doesn't matter
      }

      def clear= {
        counter := 0
        flag := False
      }
    }

    def regs: immutable.Seq[(Int, UInt)] ={
      val temp_list:ListBuffer[(Int, UInt)]= ListBuffer(0->sub_pwms(0).period)
      for(i <- channels.indices){
        temp_list.append(0x1  + i  -> channels(i).ccr)
      }

      for(i <- ccrmap_regs.indices){
        temp_list.append(0x20 + i  -> ccrmap_regs(i))
      }

      for(i <- 1 until sub_pwms.length){
        temp_list.append(0x40 + i  ->  sub_pwms(i).period)
      }

      temp_list.append(0x80->config_reg)
      temp_list.append(0x81->timeout_area.cnt_max(0))  // low 16 bits
      temp_list.append(0x82->timeout_area.cnt_max(1))  // high 16 bits
      temp_list.toList
    }

    when(pre_divicder.clk_en){
      for (i <- sub_pwms){
        i.count()
      }
    }

    for( ((name,ref),chn) <- pwm_out.elements zip channels){
      ref:= chn.output && !(timeout_area.enable && timeout_area.flag)
    }
    //pwm_out.ch1 := counter < ccr1
  }


  val apb_operate_area = new Area{
    val ok = apb.PENABLE
    val result = Reg(cloneOf(apb.PRDATA)).init(0)
    val operating = RegInit(False)
    val int_using=False
    val active=False

    apb.PSEL := 1
    apb.PADDR := 0
    apb.PWDATA := 0
    apb.PWRITE := False
    apb.PENABLE := False

    operating.toggleWhen(active)

    apb.PENABLE.setWhen(active && operating)

    when(active && !operating && !apb.PWRITE){
      result := apb.PRDATA
    }
  }

  def read(addr:UInt,high_priority:Boolean=false)(block: => Unit):Unit={
    apb.PADDR := addr.resized
    apb.PWRITE := False
    apb_operate_area.active := True
    if(high_priority){
      apb_operate_area.int_using := True
      when(apb_operate_area.ok){
        block
      }
    }else{
      when(apb_operate_area.ok && !apb_operate_area.int_using){
        block
      }
    }
  }
  def write(addr:UInt,data:Bits,high_priority:Boolean=false)(block: => Unit):Unit={
    apb.PADDR := addr.resized
    apb.PWDATA := data.resized
    apb.PWRITE := True

    apb_operate_area.active := True

    if(high_priority){
      apb_operate_area.int_using := True
      when(apb_operate_area.ok){
        block
      }
    }else{
      when(apb_operate_area.ok && !apb_operate_area.int_using){
        block
      }
    }

 }

  val int_ctrl=new Area{
    val int_ctrl_state= RegInit(U"00")
    val common_int = False
    val end_flag = False

    when(int.rise(False)){
      int_ctrl_state := 1
    }

    switch(int_ctrl_state){
      is(1){
        read(I2CRegs.interruptFlag,high_priority = true){
          int_ctrl_state := 2
        }
      }
      is(2){
        write(I2CRegs.interruptFlag,0xFFFF,high_priority = true ){
          int_ctrl_state :=0
          end_flag :=apb_operate_area.result(4 to 7).orR  // start restart end drop
          common_int := !end_flag
        }
      }
    }
  }

  def interrupt_set(config:Int)(block: =>Unit)={
    import I2CRegs.interrupt._
    write(I2CRegs.interruptConfig,restartEnable | startEnable| endEnable | dropEnable | config)(block)
  }

  val init = new Area{
    val ok = RegInit(False)
    import I2CRegs.interrupt._
    import I2CRegs._
    val state = RegInit(U("000"))
    Seq_Area(
      List(
        write(samplingClockDivider,30),
        write(timeout,0xFFFFF),
        write(tsuDat,2),
        write(filteringConfig_0,0x20 | (1<<15)), //address0 = 0x20,
        _ => ok:=True
      ),
      state
    )
  }

  val ctrl = new Area{
    def activate_tx_ack(block: =>Unit)=write(I2CRegs.txAck,0x03<<8)(block)
    def activate_tx_nack(block: =>Unit)=write(I2CRegs.txAck,0x03<<8|0x01)(block)
    def listen_rx_data(block: =>Unit)=write(I2CRegs.rxData,1<<9)(block)
    def listen_rx_ack(block: =>Unit)=write(I2CRegs.rxAck,1<<9)(block)
    def tx_data(data:Bits)(block: =>Unit)=write(I2CRegs.txData,3<<8|data.resized)(block)

    val reg_choose=new Area{
      //val regs=List(0->pwm_area.period,1->pwm_area.ccr1)
      val regs=pwm_area.regs
      val addr = Reg(UInt(8 bits)).init(0)
      val read=addr.muxList(U(0),regs)

      def write(data:Bits): Unit ={
        switch(addr){
          for (i <- regs){
            is(i._1){
              i._2.assignFromBits(data)
            }
          }
          //default{
          //  regs(0)._2.assignFromBits(data)
          //}
        }
      }
    }

    val slave_drive=new Area{
      val active=False
      val state:UInt = RegInit(U"0000")
      val data=Bits(8 bits)
      val finish = False

      data := 0
      def start(txdata:Bits)(block: => Unit)={
          active:= True
          data := txdata
          when(finish){
            block
          }
      }
      when(active){
        Seq_Area(List(
          interrupt_set(txAckEnable),
          activate_tx_nack,
          tx_data(data),
          when(int_ctrl.common_int),
          read(I2CRegs.rxData),
          block=>{
            finish := True
            block
          }
        ),
          state
        )
      }
    }

    val master_drive = new Area{
      val active=False
      val byte=Bits(8 bits)
      val finish=False
      val state:UInt = RegInit(U"0000")

      byte := apb_operate_area.result.resized
      def start(block: => Unit)={
        active := True
        when(finish)(block)
      }

      when(active){
        Seq_Area(
          List(
            interrupt_set(txAckEnable),
            block => {
              activate_tx_ack{block}
            },
            when(int_ctrl.common_int),
            read(I2CRegs.rxData),
            block => {
              finish :=True
              block
            }
          ),state

        )
      }
    }

    val fsm = new StateMachine{
      val idle_state = RegInit(U"0000")

      always{
        when(int_ctrl.end_flag){
          idle_state:=0
          master_drive.state :=0
          slave_drive.state :=0
          goto(idle)
        }
      }

      val init_state:State = new State with EntryPoint{
        whenIsActive{
          when(init.ok){
            goto(idle)
          }
        }
      }
      val idle:State = new State{
        onEntry{
          idle_state := 0
        }
        whenIsActive{
          Seq_Area(
            List(
              activate_tx_nack,   // to set tx_ack_valid to high(clear the tx ack interrupt)
              interrupt_set(txAckEnable),
              when(int_ctrl.common_int),
              block => {
                read(I2CRegs.hitOrNot){
                  when(apb_operate_area.result.lsb){
                    block
                  }otherwise{
                    idle_state := 0
                  }
                }
              },
              activate_tx_ack,
              when(int_ctrl.common_int),
              activate_tx_nack,
              block =>{
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
              master_drive.start{
                write_state:=1
                reg_temp.takeHigh(8) := master_drive.byte
              }
            }
            is(1){
              master_drive.start{
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
              slave_drive.start(reg_choose.read.takeHigh(8)){
                read_state:=1
              }
            }
            is(1){
              slave_drive.start(reg_choose.read.takeLow(8)){
                goto(idle)
              }
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
          pwm_area.timeout_area.clear
        }
        whenIsActive{
          switch(hit_state){
            is(0){
              read(I2CRegs.hitContext){
                hit_context := apb_operate_area.result.lsb
                hit_state :=1
              }
            }
            is(1){
              when(hit_context === True){
                goto(master_read)
              }otherwise{
                master_drive.start{
                  //hit_state:=2
                  reg_choose.addr := master_drive.byte.asUInt
                  goto(master_write)
                }
              }
            }
          }

        }
      }

    }
  }
}

class Gowin_OSC extends BlackBox{
  val oscout=out Bool()
}

//Hardware definition
class MyTopLevel extends Component {

  val i2c = master(I2c())
  //val ch1_out = out Bool()
  val pwm = new PWM(8)
  //val gowin= new Gowin_OSC()
  val pwm_ch_out=pwm.pwm_out.clone()
  for (i <- pwm_ch_out.elements){
    i._2.asOutput()
  }
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

  //ClockDomain.internal("").clock := gowin.out_clk

  pwm.apb <> i2c_apb.io.apb
  pwm_ch_out := pwm.pwm_out
  //ch1_out := pwm.pwm_out.elements(0)._2.as(Bool)

  pwm.int := i2c_apb.io.interrupt
  i2c <> i2c_apb.io.i2c

}

//Generate the MyTopLevel's Verilog
object MyTopLevelVerilog {
  def main(args: Array[String]) {
    //InOutWrapper

    SpinalVerilog(InOutWrapper(new MyTopLevel)).printPruned()
  }
}

//Generate the MyTopLevel's VHDL
object MyTopLevelVhdl {
  def main(args: Array[String]) {
    SpinalVhdl(new MyTopLevel)
  }
}


//Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one. This configuration can be resued everywhere
object MySpinalConfig extends SpinalConfig(defaultConfigForClockDomains = ClockDomainConfig(resetKind = ASYNC,resetActiveLevel = LOW))

//Generate the MyTopLevel's Verilog using the above custom configuration.
object MyTopLevelVerilogWithCustomConfig {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(InOutWrapper(new MyTopLevel))
  }
}