package mylib

import spinal.core.sim._
import spinal.lib.com.i2c.sim.{OpenDrainInterconnect, OpenDrainSoftConnection}
import spinal.core._
import spinal.lib.com.i2c.I2c

object DutTests {
  def main(args: Array[String]): Unit = {

    case class I2C_SimBus(i2c:I2c,freq:HertzNumber,component_freq:HertzNumber){
      val sdaInterconnect=new OpenDrainInterconnect
      val sclInterconnect=new OpenDrainInterconnect
      sdaInterconnect.addHard(i2c.sda)
      sclInterconnect.addHard(i2c.scl)

      val sda = sdaInterconnect.newSoftConnection()
      val scl = sclInterconnect.newSoftConnection()
      // 50Mhz I2c:100khz
      // 50M / 100k = 500
      def sleep_half_period=sleep((component_freq/freq/2).toLong)

      def i2c_cycle={

        sleep_half_period
        scl.write(true )
        sleep_half_period
        scl.write(false)
      }

      def i2c_cycle(sda_val:Boolean): Unit ={
        sda.write(sda_val)
        sleep_half_period
        scl.write(true)
        sleep_half_period
        scl.write(false)
        sleep_half_period
      }

      def i2c_clycle_whensampling(block: => Unit)={
        sleep_half_period
        scl.write(true)
        block
        sleep_half_period
        scl.write(false)
      }

      def start()={
        scl.write(true)
        sda.write(true)
        sleep_half_period
        sda.write(false)
        sleep_half_period
        scl.write(false)
        sleep_half_period
        sda.write(true)
      }
      def stop()={
        sda.write(false)
        sleep_half_period
        scl.write(true)
        sleep_half_period
        sda.write(true)
        sleep_half_period
      }
      def ack()={
        sda.write(false)
        i2c_cycle
        sda.write(true)
      }

      def master_send_byte(data:Int): Unit ={
        for (i <- 7 downto 0){
          if((data & 1<<i)!=0){
            i2c_cycle(true)
          }else{
            i2c_cycle(false)
          }
        }
        i2c_cycle(true)
      }

      def master_read_byte()={
        var result=0
        for(_ <- 7 downto 0 ){
          result <<= 1
          i2c_clycle_whensampling{
            if(sda.read()){
              result |= 1
            }
          }
        }
        result
      }
    }

    val compile=SimConfig.withWave.withVerilator.compile {
      val a=new MyTopLevel
      a.pwm.init.ok.simPublic()
      a.pwm.pwm_area.period.simPublic()
      a.i2c.simPublic()
      a.pwm.ctrl.fsm.stateReg.simPublic()
      a
    }

    compile.doSim("test i2c detect"){
      dut=>
        val i2cbus=I2C_SimBus(dut.i2c,100 kHz, 50 MHz)
        dut.clockDomain.forkStimulus(period = 2)
        fork {
          import dut.pwm.ctrl.fsm
          waitUntil(dut.pwm.init.ok.toBoolean)
          i2cbus.start()

          println("now state:"+ fsm.stateReg.toEnum.toString())
          assert(fsm.stateReg.toEnum == fsm.enumOf(fsm.idle))

          i2cbus.master_send_byte(0x20<<1|0x01)

          println("now state:"+ fsm.stateReg.toEnum.toString())
          assert(fsm.stateReg.toEnum == fsm.enumOf(fsm.master_read))

          i2cbus.stop()
          println("now state:"+ fsm.stateReg.toEnum.toString())
          //assert(fsm.stateReg.toEnum == fsm.enumOf(fsm.idle))

          i2cbus.start()
          i2cbus.master_send_byte(0x20<<1|0x01)
          i2cbus.stop()

        }.join()
    }

    compile.doSim("test i2c read"){dut=>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz, 50 MHz)
      dut.clockDomain.forkStimulus(period =2)
      fork {
        waitUntil(dut.pwm.init.ok.toBoolean)
        i2cbus.start()
        i2cbus.master_send_byte(0x31)
        i2cbus.stop()


        i2cbus.start()
        i2cbus.master_send_byte(0x40)
        i2cbus.master_send_byte(0x01)
        i2cbus.start()
        i2cbus.master_send_byte(0x41)
        val byte1 = i2cbus.master_read_byte()
        i2cbus.ack()
        val byte2 = i2cbus.master_read_byte()
        i2cbus.ack()

        println(f"recv $byte1%x")
        println(f"recv $byte2%x")

        assert((byte1<<8 | byte2)==0x03e8)
      }.join()
    }

    compile.doSim("test i2c write"){ dut =>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz,50 MHz)
      dut.clockDomain.forkStimulus(period = 2)
      fork{
        dut.clockDomain.waitSampling()
        waitUntil(dut.pwm.init.ok.toBoolean == true)
        i2cbus.start()
        i2cbus.sleep_half_period
        i2cbus.master_send_byte(0x40)
        i2cbus.master_send_byte(0x00)
        i2cbus.master_send_byte(0xff)
        i2cbus.master_send_byte(0xbb)
      }.join()

      assert(dut.pwm.pwm_area.period.toInt == 0xFFBB,"error,the period should be 0xFFBB")
    }
  }
}
