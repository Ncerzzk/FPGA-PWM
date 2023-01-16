package mylib

import spinal.core.sim._
import spinal.lib.com.i2c.sim.{OpenDrainInterconnect, OpenDrainSoftConnection}
import spinal.core._
import spinal.lib.com.i2c.I2c

import scala.collection.mutable.ListBuffer

object SPI_PWM_DutTests {
  def main(args: Array[String]): Unit = {
    val compile=SimConfig.withWave.withVerilator.compile {
      val a=new SPI_PWM_Top
      a
    }

    compile.doSim("test apb operation"){
      dut=>
        dut.clockDomain.forkStimulus(period = 2)
        fork{
          dut.clockDomain.waitSampling(10)
        }.join()
    }
  }
}

