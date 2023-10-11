package mylib

import spinal.core.sim._
import spinal.lib.com.i2c.sim.{OpenDrainInterconnect, OpenDrainSoftConnection}
import spinal.core._

import scala.collection.mutable.ListBuffer
import spinal.lib.com.spi.{SpiSlave, SpiSlaveCtrlGenerics, SpiSlaveCtrlMemoryMappedConfig}
import spinal.lib.fsm.{State, StateFsm}
import spinal.lib.io.TriStateOutput

object SPI_DShot_DutTests {
  def main(args: Array[String]): Unit = {
    val compile=SimConfig.withWave.withVerilator.compile {
      val a=new SPI_DShot_Top(false,3)
      a.spi_dshot.regs.data.simPublic()
      a.spi_dshot.spi_fsm.stateReg.simPublic()
      a.spi_dshot.dshot.out_enables.simPublic()
      a.spi_dshot.dshot.d_out.simPublic()
      a.spi_dshot.dshot.reloading.simPublic()
      a
    }

    def testSPIMaster(spipins:SpiSlave)=simSpiMaster(spipins,60,5,3)

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
          //waitUntil(dut.spi_dshot.inited.toBoolean)
          sleep(100)
          master.transfer(Array(1,0xff,3))
          sleep(100)
        }.join()

        assert(dut.spi_dshot.regs.data.getBigInt(0)==0xff03)
    }

    def spi_read_test(addr:Int): Unit ={
      compile.doSim("spi read" + addr.toString()){
        dut=>
          dut.clockDomain.forkStimulus(period = 2)
          val master = testSPIMaster(dut.spi_pins)
          val data = scala.util.Random.nextInt(65536)
          var ret:Seq[Int] = Array[Int]()

          val true_addr = dut.spi_dshot.regs.decode(addr)
          dut.spi_dshot.regs.data.setBigInt(true_addr,data)

          fork{
            sleep(50)
            ret = master.transfer(Array( addr<<1 | 0,0,0))
            sleep(50)

          }.join()
          val result = ret(1)<<8 | ret(2)
          println(f"target:$data val:$result")
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
          import dut.spi_dshot.spi_fsm._
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
    spi_corrupt_test(compile.dut.spi_dshot.spi_fsm.start_transfer)
    spi_corrupt_test(compile.dut.spi_dshot.spi_fsm.being_written)

    compile.doSim("continuous write test"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        val master = testSPIMaster(dut.spi_pins)
        dut.clockDomain.waitSampling(50)
        val ret = master.transfer(Array( 1<<1 | 1,2,3,4,5,6,7))
        dut.clockDomain.waitSampling(50)

        val decode: Int=>Int = dut.spi_dshot.regs.decode

        assert(dut.spi_dshot.regs.data.getBigInt(decode(1)) == 0x0203)
        assert(dut.spi_dshot.regs.data.getBigInt(decode(2)) == 0x0405)
        assert(dut.spi_dshot.regs.data.getBigInt(decode(3)) == 0x0607)
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

    def get_dshot_val(dut:SPI_DShot_Top,channel:Int, val1ccr:Int,predivide:Int = 1)={
      /*
      channel start from 0
       */
      var predivide_cnt=0
      var cnt=0
      var result = 0

      waitUntil(((dut.spi_dshot.dshot.out_enables.toInt >> channel) & 1) == 1)
      while(((dut.spi_dshot.dshot.out_enables.toInt >> channel) & 1) == 1){
        dut.clockDomain.waitSampling()
        if( ((dut.spi_dshot.dshot.d_out.toInt >> channel) & 1 ) == 1){
          predivide_cnt += 1
          if(predivide_cnt == predivide){
            cnt += 1
            predivide_cnt = 0
          }
        }

        if(dut.spi_dshot.dshot.reloading.toBoolean){
          result <<= 1
          if(cnt == val1ccr){
            result |= 1
          }
          cnt = 0
        }
      }
      result
    }
    def dshot_test(value:Int, channel:Int,predivide:Int = 1): Unit ={
      /*
      channel start from 0
       */
      compile.doSim(f"dshot test for ${value.toHexString}"){
        dut=>
          dut.clockDomain.forkStimulus(period = 2)
          val master = testSPIMaster(dut.spi_pins)
          dut.clockDomain.waitSampling(50)
          master.transfer(Array(0x0 << 1 | 1, 0x00, 10))  //   write 10 to period
          dut.clockDomain.waitSampling(50)
          master.transfer(Array(0x60 << 1 | 1,0x00, 7))  // write 7 to ccr1
          dut.clockDomain.waitSampling(50)
          master.transfer(Array(0x50 << 1 | 1,0x00, predivide -1 ))
          dut.clockDomain.waitSampling(50)
          val high = value >> 8
          val low = value & 0xff
          master.transfer(Array( (channel+1) << 1 | 1,high, low)) // write dshot value of channel , and trigger it

          var result:Int = 0
          fork{
            result = get_dshot_val(dut,channel,7,predivide)
          }
          dut.clockDomain.waitSampling(1000 * predivide)
          println(f"result :${result.toHexString}")
          assert(result == value)
      }
    }

    dshot_test(0xff00,0)
    dshot_test(0xff00,1)
    dshot_test(scala.util.Random.nextInt(65536),2)
    dshot_test(scala.util.Random.nextInt(65536),3,2)
  }

}
