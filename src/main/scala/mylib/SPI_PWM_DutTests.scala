package mylib

import spinal.core.sim._
import spinal.lib.com.i2c.sim.{OpenDrainInterconnect, OpenDrainSoftConnection}
import spinal.core._

import scala.collection.mutable.ListBuffer
import spinal.lib.com.spi.{SpiSlave, SpiSlaveCtrlGenerics, SpiSlaveCtrlMemoryMappedConfig}
import spinal.lib.fsm.{State, StateFsm}
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
    sleep(sleepCnt)
    ss #= true
    ret
  }
}
object SPI_PWM_DutTests {
  def main(args: Array[String]): Unit = {
    val compile=SimConfig.withWave.withVerilator.compile {
      val a=new SPI_PWM_Top
      a.spi_pwm.regs.data.simPublic()
      a.spi_pwm.spi_fsm.stateReg.simPublic()
      a
    }

    def testSPIMaster(spipins:SpiSlave)=simSpiMaster(spipins,240,5)

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
        val master = testSPIMaster(dut.spi_pins)

        fork{
          //waitUntil(dut.spi_pwm.inited.toBoolean)
          sleep(100)
          master.transfer(Array(1,0xff,3))
          sleep(100)
        }.join()

        assert(dut.spi_pwm.regs.data.getBigInt(0)==0xff03)
    }

    def spi_read_test(addr:Int): Unit ={
      compile.doSim("spi read" + addr.toString()){
        dut=>
          dut.clockDomain.forkStimulus(period = 2)
          val master = testSPIMaster(dut.spi_pins)
          val data = scala.util.Random.nextInt(65536)
          var ret:Seq[Int] = Array[Int]()
          dut.spi_pwm.regs.data.setBigInt(addr,data)

          fork{
            sleep(50)
            ret = master.transfer(Array( addr<<1 | 0,0,0))
            sleep(50)

          }.join()
          assert((ret(1)<<8 | ret(2)) == data)
      }
    }

    spi_read_test(0)
    spi_read_test(1)
    spi_read_test(2)


    def spi_corrupt_test(state:State)={
      compile.doSim("spi corrupt at " + state.toString().split("/").last){
        dut=>
          dut.clockDomain.forkStimulus(period = 2)
          val master = testSPIMaster(dut.spi_pins)
          import dut.spi_pwm.spi_fsm._
          var has_pull_up_ss=false
          forkSensitive(stateReg){
            if(!has_pull_up_ss && enumOf(state)==stateReg.toEnum){
              master.ss #= true
              has_pull_up_ss=true
            }else{
              if(has_pull_up_ss){
                assert(enumOf(idle)==stateReg.toEnum)
              }
            }
          }

          fork{
            sleep(50)
            master.transfer(Array(1,2,3))
            sleep(50)
          }.join()
      }
    }
    spi_corrupt_test(compile.dut.spi_pwm.spi_fsm.start_transfer)
    spi_corrupt_test(compile.dut.spi_pwm.spi_fsm.being_written)

    compile.doSim("continuous write test"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        val master = testSPIMaster(dut.spi_pins)
        dut.clockDomain.waitSampling(50)
        val ret = master.transfer(Array( 1<<1 | 1,2,3,4,5,6,7))
        dut.clockDomain.waitSampling(50)

        assert(dut.spi_pwm.regs.data.getBigInt(1) == 0x0203)
        assert(dut.spi_pwm.regs.data.getBigInt(2) == 0x0405)
        assert(dut.spi_pwm.regs.data.getBigInt(3) == 0x0607)
    }

    compile.doSim("read write after a corrupt write sequence"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        val master = testSPIMaster(dut.spi_pins)
        dut.clockDomain.waitSampling(50)
        master.transfer(Array(0x2 | 1,0x07))  //   write 1 byte and then stop.
        master.transfer(Array(0x0 | 1,0x07,0xd0))  // write 2000 to reg 0
        master.transfer(Array(0x1<<1 | 1,0x03,0xe8)) // write 1000 to reg 1

        def get_data(ret:Seq[Int])={
          ret(1) <<8 | ret(2)
        }
        var ret = master.transfer(Array(0x00 | 0,0,0))
        println(get_data(ret).toHexString)
        assert(get_data(ret) == 2000)
        ret = master.transfer(Array(0x01<<1 | 0,0,0))
        assert(get_data(ret) == 1000)
        dut.clockDomain.waitSampling(50)

    }
  }

}

