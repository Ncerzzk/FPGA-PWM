package mylib

import spinal.core.sim._
import spinal.lib.com.i2c.sim.{OpenDrainInterconnect, OpenDrainSoftConnection}
import spinal.core._
import spinal.lib.com.i2c.I2c

object DutTests {
  def main(args: Array[String]): Unit = {
    def sleep_half_period=sleep(350)

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

      def i2c_clycle_whensampling(block: => Unit)={
        sleep_half_period
        scl.write(true)
        block
        sleep_half_period
        scl.write(false)
      }

      def start()={
        scl.write(true)
        sleep_half_period
        sda.write(false)
        sleep_half_period
        scl.write(false)
        sleep_half_period
      }

      def ack()={
        sda.write(false)
        i2c_cycle
      }

      def master_send_byte(data:Int): Unit ={
        for (i <- 7 downto 0){
          if((data & 1<<i)!=0){
            sda.write(true)
          }else{
            sda.write(false)
          }
          i2c_cycle
        }
        i2c_cycle
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
      a.a.init.ok.simPublic()
      a.a.pwm_area.period.simPublic()
      a.i2c.simPublic()
      a
    }

    compile.doSim("test i2c read"){dut=>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz, 50 MHz)
      dut.clockDomain.forkStimulus(period =2)
      fork {
        waitUntil(dut.a.init.ok.toBoolean)
        i2cbus.start()
        i2cbus.master_send_byte(0x41)
        i2cbus.master_send_byte(0x01)
        val byte1 = i2cbus.master_read_byte()
        i2cbus.ack()
        val byte2 = i2cbus.master_read_byte()

        print(f"recv $byte1%x")
      }.join()
    }

    compile.doSim("test i2c write"){ dut =>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz,50 MHz)
      dut.clockDomain.forkStimulus(period = 2)
      fork{
        dut.clockDomain.waitSampling()
        waitUntil(dut.a.init.ok.toBoolean == true)
        i2cbus.start()
        sleep_half_period
        i2cbus.master_send_byte(0x40)
        i2cbus.master_send_byte(0x02)
        i2cbus.master_send_byte(0xff)
        i2cbus.master_send_byte(0xbb)
      }.join()

      assert(dut.a.pwm_area.period.toInt == 0xFFBB,"error,the period should be 0xFFBB")
    }
  }
}
