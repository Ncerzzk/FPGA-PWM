package mylib

import spinal.core._
import spinal.core.sim._

import scala.collection.immutable
import scala.collection.mutable.ListBuffer

class SubPWM(enable:Bool,val period:UInt =Reg(UInt(16 bits)).init(2000) ) extends Area{
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

object PWMArea{

  def getPWMRegs(regs:RegMem,subPWMNum:Int,pwmChannelNum:Int)=new Area{
    regs.add(0x0,"subpwm0_period","")
    val ccrs_regs:ListBuffer[Bits]=ListBuffer()
    val pwm_channel_map_regs:ListBuffer[Bits]=ListBuffer()
    val subpwm_period_regs:ListBuffer[Bits]=ListBuffer()

    subpwm_period_regs+=regs(0)

    for(i <- 0 until pwmChannelNum){
      regs.add(i+1,s"CCR${i}","")
      ccrs_regs += regs(i+1)
    }

    for(i<- 0 until 1 + pwmChannelNum*2/16){
      regs.add(0x20+i,s"pwm_channel_map${i}","")
      pwm_channel_map_regs += regs(0x20+i)
    }

    for(i<- 0 until subPWMNum-1){
      regs.add(0x40+i,s"subpwm${i}_period","")
      subpwm_period_regs += regs(0x40+i)
    }

    regs.add(0x50,"config","")
    val config=regs(0x50)

    regs.add(0x7F,"watchdog","")
    val watchdog=regs(0x7F)

    regs.add(0x7E,"timeout max high","")
    regs.add(0x7D,"timeout max low","")
    val timeout_max_high=regs(0x7E)
    val timeout_max_low=regs(0x7D)


  }
}

class PWMArea(val regs:RegMem,channel_num:Int=4, sub_PWM_num:Int=2,timeout_ext:Boolean=true) extends Area {
  assert(sub_PWM_num >= 1)
  val pwm_out = new Bundle {
    for (i <- 0 until channel_num) {
      valCallback(out Bool(), "ch" + (i).toString)
    }
  }

  val pwm_regs = PWMArea.getPWMRegs(regs, sub_PWM_num, channel_num)

  val sub_pwms = ListBuffer(new SubPWM(True,pwm_regs.subpwm_period_regs(0).asUInt))
  for (i <- 1 until sub_PWM_num) {
    sub_pwms.append(new SubPWM(pwm_regs.config(15 - i),pwm_regs.subpwm_period_regs(i).asUInt)) // 14 for sub_pwm(1) | 13 for sub_pwm(2)   ...
  }

  class PWMChannel(channel_idx:Int,config: Bits) extends Area {
    val output = Bool
    val counter_map = config.muxList(U(0), for (index <- 0 until sub_PWM_num) yield (index, sub_pwms(index).counter))

    output := counter_map < pwm_regs.ccrs_regs(channel_idx).asUInt
  }

  val pre_divicder = new Area {
    val counter = Reg(UInt(5 bits)).init(0)
    val divider_val = pwm_regs.config.takeLow(5).asUInt
    val clk_en = False.setWhen(counter === divider_val)

    counter := counter + 1
    when(counter === divider_val) {
      counter := 0
    }
  }

  val pwm_area = new Area {

    val channels = for (i <- 0 until channel_num) yield {
      val range = (1 + i % 8 * 2) downto (0 + i % 8 * 2)
      new PWMChannel(i,pwm_regs.pwm_channel_map_regs(i / 8)(range).asBits)
    }

    val timeout_area = timeout_ext.generate(new Area {
      val cnt_max = Vec(pwm_regs.timeout_max_low, pwm_regs.timeout_max_high)
      val counter = Reg(UInt(32 bits)).init(0)
      val enable = pwm_regs.config.msb
      val flag = Reg(Bool()).init(False)

      val lastwdt=RegNext(pwm_regs.watchdog.lsb,False)
      val wdt_change=RegNext(pwm_regs.watchdog.lsb ^ lastwdt)

      when(enable && !flag){
        counter := counter + 1
      }elsewhen(wdt_change){
        counter := 0
      }

      flag.setWhen(counter === cnt_max.asBits.asUInt).clearWhen(wdt_change)
      // the counter would stop at cnt_max +1 , but it doesn't matter

    })

    when(pre_divicder.clk_en) {
      for (i <- sub_pwms) {
        i.count()
      }
    }

    var output_enable: Bool = True
    if (timeout_ext) {
      output_enable = !(timeout_area.enable && timeout_area.flag)
    }
    for (((name, ref), chn) <- pwm_out.elements zip channels) {
      ref := chn.output && output_enable
    }
    //pwm_out.ch1 := counter < ccr1
  }
}

class PWMTestComponent extends Component{
  val regs=new RegMem(16 bits,16)
  val pwm = new PWMArea(regs,8,2)

}

object PWM_DutTests {
  def main(args: Array[String]): Unit = {
    val compile = SimConfig.withWave.withVerilator.compile {
      val a = new PWMTestComponent
      a.regs.data.simPublic()
      a.pwm.sub_pwms(0).counter.simPublic()
      a.pwm.pwm_area.timeout_area.counter.simPublic()
      a.pwm.pwm_area.timeout_area.flag.simPublic()
      a
    }

    compile.doSim("pwm basic test"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        dut.regs.data.setBigInt(0,99)
        //dut.regs.data(0) #= 99
        for(i<- 0 until 8){
          dut.regs.data.setBigInt(i+1,i*10)
          //dut.regs.data(i+1) #= i*10
          forkSensitive{
            if(dut.pwm.sub_pwms(0).counter.toInt < i*10){
              assert(dut.pwm.pwm_out.elements(i)._2.asInstanceOf[Bool].toBoolean)
            }else{
              assert(!dut.pwm.pwm_out.elements(i)._2.asInstanceOf[Bool].toBoolean)
            }
          }
        }
        dut.clockDomain.waitSamplingWhere(dut.pwm.sub_pwms(0).counter.toInt == 99)
        dut.clockDomain.waitSampling()
        assert(dut.pwm.sub_pwms(0).counter.toInt == 0)
        dut.clockDomain.waitSampling(150)
    }

    compile.doSim("pwm timeout"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        dut.regs.data.setBigInt(0,99)   // set period
        dut.regs.data.setBigInt(1,100)  // set ccr0
        dut.regs.data.setBigInt(dut.regs.decode(0x7e),0)  // set timeout max high 16 bits
        dut.regs.data.setBigInt(dut.regs.decode(0x7d),0xff) // set timeout max low 16 bits
        dut.regs.data.setBigInt(dut.regs.decode(0x50),1 << 15) // set timeout enable

        waitUntil(dut.pwm.pwm_area.timeout_area.counter.toBigInt == 10)
        assert(!dut.pwm.pwm_area.timeout_area.flag.toBoolean)
        assert(dut.pwm.pwm_out.elements(0)._2.asInstanceOf[Bool].toBoolean)


        dut.clockDomain.waitSampling(0xff+10)
        assert(dut.pwm.pwm_area.timeout_area.flag.toBoolean)
        assert(!dut.pwm.pwm_out.elements(0)._2.asInstanceOf[Bool].toBoolean)

        dut.regs.data.setBigInt(dut.regs.decode(0x7f),1)
        dut.clockDomain.waitSampling(10)
        assert(!dut.pwm.pwm_area.timeout_area.flag.toBoolean)
        assert(dut.pwm.pwm_out.elements(0)._2.asInstanceOf[Bool].toBoolean)

        dut.clockDomain.waitSampling(0xff+10)
        dut.regs.data.setBigInt(dut.regs.decode(0x7f),0)
        dut.clockDomain.waitSampling(10)
        assert(!dut.pwm.pwm_area.timeout_area.flag.toBoolean)
        assert(dut.pwm.pwm_out.elements(0)._2.asInstanceOf[Bool].toBoolean)

    }
  }

}