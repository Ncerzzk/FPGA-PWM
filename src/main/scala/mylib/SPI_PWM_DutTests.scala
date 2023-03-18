package mylib

import spinal.core.sim._
import spinal.lib.com.i2c.sim.{OpenDrainInterconnect, OpenDrainSoftConnection}
import spinal.core._

import scala.collection.mutable.ListBuffer
import spinal.lib.com.spi.{SpiSlave, SpiSlaveCtrlGenerics, SpiSlaveCtrlMemoryMappedConfig}
import spinal.lib.io.TriStateOutput

import scala.collection.mutable
case class simSpiMaster(spi:SpiSlave,moduleFreq:Int,spiFreq:Int){
  val mosi=spi.mosi
  val sclk=spi.sclk
  val miso=spi.miso.write
  val ss = spi.ss
  val sleepCnt: Int = moduleFreq / spiFreq

  ss #= true
  sclk #= false
  def transferSampleByte(data:Int): Int ={
    var ret:Int=0
    for(i <- 0 until 8 ){
      val level = data >> (7-i) & 1
      if(level != 0){
        mosi #= true
      }else{
        mosi #= false
      }
      sleep(sleepCnt)  // wait sclk posedge
      sclk #= true
      ret <<= 1
      if(miso.toBoolean){
        ret |= 1
      }
      sleep(sleepCnt)
      sclk #= false
      sleep(sleepCnt)
    }
    ret
  }
  def transfer(dataArray: Array[Int]): mutable.Seq[Int] ={
    val ret = ListBuffer[Int]()
    sleep(sleepCnt)
    ss #= false
    sleep(sleepCnt)
    for(data <- dataArray){
      ret += transferSampleByte(data)
    }
    ss #= true
    ret
  }
}
object SPI_PWM_DutTests {
  def main(args: Array[String]): Unit = {
    val compile=SimConfig.withWave.withVerilator.compile {
      val a=new SPI_PWM_Top
      a.spi_pwm.regs.data.simPublic()
      a
    }

    compile.doSim("test apb operation"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        fork{
          dut.clockDomain.waitSampling(10)
        }.join()
    }

    compile.doSim("spi transfer"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        val master = simSpiMaster(dut.spi_pins,200,50)

        fork{
          //waitUntil(dut.spi_pwm.inited.toBoolean)
          sleep(100)
          master.transfer(Array(1,2,3))
          sleep(100)
        }.join()

        assert(dut.spi_pwm.regs.data.getBigInt(0)==0x0203)
    }

    compile.doSim("spi read"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        val master = simSpiMaster(dut.spi_pins,200,50)
        dut.spi_pwm.regs.data.setBigInt(0,0xf1f2)

        fork{
          sleep(50)
          val ret = master.transfer(Array(1<<1 | 0,0,0))
          sleep(50)
          for(i<- ret){
            println(s"${i}")
          }
        }.join()
    }


  }
}

