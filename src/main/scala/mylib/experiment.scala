package mylib
import spinal.core._

import scala.collection.mutable


class Experiment extends Component {
  val addr=in UInt(8 bits)
  val data = in Bits(16 bits)
  val s= new RegMem(16 bits,10)

  s.add(0x3,"CCR1","")
  s.add(0x4,"CCR2","")

  s.add(0xff,"Period","")

  s(addr) :=  data


}

object Experiment {
  def main(args: Array[String]) {
    //InOutWrapper
    SpinalVerilog(new Experiment).printPruned()
  }
}
