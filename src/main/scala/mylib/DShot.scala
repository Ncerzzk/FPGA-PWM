package mylib

import mylib.DShot.getDShotRegs
import spinal.core._
import spinal.core.sim._
import spinal.lib.{Counter, StreamFifo}

import scala.collection.immutable
import scala.collection.mutable.ListBuffer
object DShot{
  def getDShotRegs(regs:RegMem,channelNum:Int)=new Area{
    val dshot_vals:ListBuffer[Bits]=ListBuffer()
    regs.add(0x0,"period","")
    val period = regs(0x00).asUInt

    regs.add(0x60,"value1ccr","")
    val val1ccr = regs(0x60).asUInt

    val val0ccr: UInt = val1ccr |>> 1  // why?

    regs.add(0x50,"config","")
    val config = regs(0x50)

    for(i <- 0 until channelNum){
      regs.add(i+1,s"Val${i}",s"dshot value of channel ${i}")
      dshot_vals += regs(i+1)
    }
  }

  def getPWMOutInterfacec(pwmChannelNum:Int)=new Bundle{
    for (i <- 0 until pwmChannelNum) {
      valCallback(out Bool(), "ch" + (i).toString)
    }
  }
}
class DShot(val regs:RegMem,channelNum:Int=8) extends Area{
  val dshot_regs = getDShotRegs(regs,channelNum)
  val trigers = U(0,channelNum bits)
  val trigers_shadow = Reg(cloneOf(trigers)).init(0)
  val out_enables = Reg(Bits(channelNum bits)).init(0)
  val d_out = out(Bits(channelNum bits))
  val dshot_ptrs =ListBuffer.fill(channelNum)(Reg(UInt(4 bits)).init(0))

  val pre_divider = new Area {
    val counter = Reg(UInt(5 bits)).init(0)
    val divider_val = dshot_regs.config.takeLow(5).asUInt
    val clk_en = False

    counter := counter + 1
    when(counter === divider_val) {
      counter := 0
      clk_en := True
    }
  }

  // val counter = Reg(UInt(16 bits)).init(0)
  val counter = Counter(16 bits)

  val reloading = False
  when(pre_divider.clk_en){
    counter.increment()
    when(counter.value === dshot_regs.period){
      counter.clear()
      reloading := True
    }
  }

  for( i<- 0 until channelNum){
    out_enables(i).clearWhen(dshot_ptrs(i) === 0 && reloading)
    out_enables(i).setWhen(trigers_shadow(i))
    trigers_shadow(i).setWhen(trigers(i).rise())
    trigers_shadow(i).clearWhen(reloading && trigers_shadow(i))
    when(reloading){
      when(trigers_shadow(i)){
        dshot_ptrs(i) := 0xF
      }otherwise{
        dshot_ptrs(i) :=  (dshot_ptrs(i)===0 ) ? U(0) | (dshot_ptrs(i) - 1)
      }
    }
    when(out_enables(i)){
      val ccr = Mux( (dshot_regs.dshot_vals(i) >> dshot_ptrs(i)).lsb, dshot_regs.val1ccr , dshot_regs.val0ccr)
      d_out(i) := ( (counter < ccr) ? True | False)
    }otherwise{
      d_out(i) := False
    }
  }


}

class DShotTestComponent extends Component{
  val regs=new RegMem(16 bits,16)
  val dshot = new DShot(regs,8)
}

object DShot_DutTests {
  def main(args: Array[String]): Unit = {
    val compile = SimConfig.withWave.withVerilator.compile {
      val a = new DShotTestComponent
      a.regs.data.simPublic()
      a.dshot.trigers.simPublic()
      a.dshot.regs.data.simPublic()
      a.dshot.d_out.simPublic()
      a.dshot.counter.value.simPublic()
      a.dshot.reloading.simPublic()
      a.dshot.out_enables.simPublic()
      a
    }

    compile.doSim("dshot basic") {
      dut =>
        dut.clockDomain.forkStimulus(period = 2)
        dut.regs.data.setBigInt(0, 10)
        dut.regs.data.setBigInt(dut.regs.decode(0x60), 7)
        dut.regs.data.setBigInt(dut.regs.decode(0x1), 0xff00)
        dut.dshot.trigers #= 0x0
        dut.clockDomain.waitSampling(20)
        dut.dshot.trigers #= 0xff

        var cnt = 0
        var result = 0
        dut.clockDomain.onRisingEdges{
          if((dut.dshot.out_enables.toInt & 1) == 1){
            if((dut.dshot.d_out.toInt & 1) == 1){
              cnt += 1
            }
            if(dut.dshot.reloading.toBoolean){
              result <<= 1
              if(cnt == 7){
                result |= 1
              }
              cnt = 0
            }
          }
        }

        dut.clockDomain.waitSampling(500)
        assert(result == 0xff00)
    }
  }
}
