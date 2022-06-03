package mylib

import spinal.core.sim._
import spinal.lib.com.i2c.sim.{OpenDrainInterconnect, OpenDrainSoftConnection}
import spinal.core._
import spinal.lib.com.i2c.I2c

import scala.collection.mutable.ListBuffer

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
        sda.write(true)
      }

      def i2c_clycle_whensampling(block: => Unit)={
        sleep_half_period
        scl.write(true)
        block
        sleep_half_period
        scl.write(false)
        sleep_half_period
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
      def ack()=i2c_cycle(false)
      def nack()=i2c_cycle(true)

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

      def master_read_byte(ack:Boolean)={
        var result=0
        for(_ <- 7 downto 0 ){
          result <<= 1
          i2c_clycle_whensampling{
            if(sda.read()){
              result |= 1
            }
          }
        }
        i2c_cycle(!ack)
        result
      }
      def detect(addr:Int)={
        start()
        master_send_byte( addr<<1 |0x00)
        stop()
      }
      def master_read(addr:Int,reg:Int,len:Int) ={
        start()
        master_send_byte(addr<<1 | 0x00)
        master_send_byte(reg)
        start()
        master_send_byte(addr<<1 | 0x01)
        var result=0
        for(_ <- 1 until len){
          result = result<<8 | master_read_byte(true)
        }
        result= result<<8 | master_read_byte(false)
        stop()
        result
      }
      def master_write(addr:Int,reg:Int,data:Int) ={
        start()
        master_send_byte(addr<<1 | 0x00)
        master_send_byte(reg)
        master_send_byte(data>>8)
        master_send_byte(data&0xFF)
        stop()
      }
    }

    val compile=SimConfig.withWave.withVerilator.compile {
      val a=new MyTopLevel
      a.pwm.init.ok.simPublic()
      a.pwm.pwm_area.period.simPublic()
      a.pwm.pwm_area.ccrs(0).simPublic()
      a.pwm.pwm_area.counter.simPublic()
      a.pwm.pwm_out.simPublic()
      a.i2c.simPublic()
      a.pwm.ctrl.fsm.stateReg.simPublic()
      a.i2c_apb.bridge.rxData.value.simPublic()
      a
    }

    compile.doSim("test read after write"){
      dut=>
        val i2cbus=I2C_SimBus(dut.i2c,100 kHz, 50 MHz)
        dut.clockDomain.forkStimulus(period = 2)
        fork{
          i2cbus.master_write(0x20,0x00,0xffbb)
          val result=i2cbus.master_read(0x20,0x00,2)
          assert(result==0xffbb)
        }.join()
    }

    compile.doSim("not call you"){
      dut=>
        val i2cbus=I2C_SimBus(dut.i2c,100 kHz,50 MHz)
        dut.clockDomain.forkStimulus(period=2)
        import dut.pwm.ctrl.fsm._
        waitUntil(dut.pwm.init.ok.toBoolean)
        forkSensitive(stateReg.toBigInt){
          assert(enumOf(hit) != stateReg.toEnum)
        }
        fork{
          i2cbus.master_write(0x01,0x20<<1,0x2020)
          i2cbus.master_write(0x00,0x20,0xffbb)
          i2cbus.master_write(0x21,0x00,0xffbb)
          i2cbus.master_write(0x21,0x20,0xffbb)
        }.join()


    }

    compile.doSim("test read many times"){
      dut=>
        val i2cbus=I2C_SimBus(dut.i2c,100 kHz, 50 MHz)
        dut.clockDomain.forkStimulus(period = 2)
        fork{
          var  result_list=ListBuffer.empty[Int]
          for(i<- 0 to 1){
            val result=i2cbus.master_read(0x20,i,2)
            result_list += result
          }
          assert(result_list(0)==2000)
          assert(result_list(1)==0)
        }.join()

    }

    compile.doSim("test i2c detect a range addr"){
      dut=>
        val i2cbus=I2C_SimBus(dut.i2c,100 kHz, 50 MHz)
        dut.clockDomain.forkStimulus(period = 2)
        import dut.pwm.ctrl.fsm._
        import dut.i2c_apb.bridge.rxData
        forkSensitive(stateReg.toEnum){
          if(enumOf(hit)==stateReg.toEnum){
            println("now hit")
            assert(rxData.value.toLong == 0x40)
          }
        }
        fork{
          for(i<- 1 to 127){
           i2cbus.detect(i)
          }
        }.join()
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

          i2cbus.master_send_byte(0x20<<1|0x00)   // we should use write to probe i2c slaves

          println("now state:"+ fsm.stateReg.toEnum.toString())
          assert(fsm.stateReg.toEnum == fsm.enumOf(fsm.hit))

          i2cbus.stop()
          println("now state:"+ fsm.stateReg.toEnum.toString())
          assert(fsm.stateReg.toEnum == fsm.enumOf(fsm.idle))

          i2cbus.start()
          i2cbus.master_send_byte(0x20<<1|0x00)
          i2cbus.stop()
          i2cbus.sleep_half_period

        }.join()
    }

    compile.doSim("test i2c read"){dut=>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz, 50 MHz)
      dut.clockDomain.forkStimulus(period =2)
      fork {
        waitUntil(dut.pwm.init.ok.toBoolean)

        i2cbus.start()
        i2cbus.master_send_byte(0x40)
        i2cbus.master_send_byte(0x00)
        i2cbus.start()
        i2cbus.master_send_byte(0x41)
        val byte1 = i2cbus.master_read_byte(true)
        val byte2 = i2cbus.master_read_byte(false)
        i2cbus.stop()

        println(f"recv $byte1%x")
        println(f"recv $byte2%x")

        assert((byte1<<8 | byte2)==0x07d0)
      }.join()
    }

    compile.doSim("test i2c write"){ dut =>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz,50 MHz)
      dut.clockDomain.forkStimulus(period = 2)
      fork{
        dut.clockDomain.waitSampling()
        waitUntil(dut.pwm.init.ok.toBoolean)
        i2cbus.master_write(0x20,0x00,0xffbb)
      }.join()

      assert(dut.pwm.pwm_area.period.toInt == 0xFFBB,"error,the period should be 0xFFBB")
    }

    compile.doSim("test no predivider"){dut=>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz,50 MHz)
      dut.clockDomain.forkStimulus(period = 2)
      fork{
        waitUntil(dut.pwm.init.ok.toBoolean)
        i2cbus.master_write(0x20,0x01,1000)
        println("start cnt:"+dut.pwm.pwm_area.counter.toInt.toString)
        for(i <- 0 to 100){
          dut.clockDomain.waitSampling()
        }
        println("start stop:"+dut.pwm.pwm_area.counter.toInt.toString)
      }.join()
    }

    compile.doSim("test predivider"){dut=>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz,50 MHz)
      val predivider_val=4
      val cnt=100
      dut.clockDomain.forkStimulus(period = 2)
      fork{
        waitUntil(dut.pwm.init.ok.toBoolean)
        i2cbus.master_write(0x20,0x01,1000)
        i2cbus.master_write(0x20,0x80,predivider_val)
        val start_cnt=dut.pwm.pwm_area.counter.toInt
        for(i <- 0 to cnt){
          dut.clockDomain.waitSampling()
        }
        assert(dut.pwm.pwm_area.counter.toInt-start_cnt==cnt/(predivider_val+1))
      }.join()
    }

    compile.doSim("test timeout"){dut=>
      val i2cbus=I2C_SimBus(dut.i2c,100 kHz,50 MHz)
      dut.clockDomain.forkStimulus(period = 2)
      fork{
        waitUntil(dut.pwm.init.ok.toBoolean)
        i2cbus.master_write(0x20,0x00,200)
        i2cbus.master_write(0x20,0x01,100)
        for( i <- 0 to 200){
          if(dut.pwm.pwm_area.counter.toInt < 100){
            assert(dut.pwm.pwm_out.elements(0)._2.asInstanceOf[Bool].toBoolean)
          }else{
            assert(! dut.pwm.pwm_out.elements(0)._2.asInstanceOf[Bool].toBoolean)
          }
          dut.clockDomain.waitSampling()
        }
        i2cbus.master_write(0x20,0x81,300)
        i2cbus.master_write(0x20,0x80,1<<15)
        dut.clockDomain.waitSampling(300)
        for(_ <- 0 to 200){
          assert(! dut.pwm.pwm_out.elements(0)._2.asInstanceOf[Bool].toBoolean)
          dut.clockDomain.waitSampling()
        }
        i2cbus.master_write(0x20,0x81,0xFFFF)
        i2cbus.master_write(0x20,0x80,1<<15)
      }.join()
    }
  }
}
