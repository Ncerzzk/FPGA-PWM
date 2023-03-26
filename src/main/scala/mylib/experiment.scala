package mylib
import spinal.core._

import scala.collection.mutable


class Experiment extends Component {
  val a, b, c = UInt(4 bits)
  a := 0
  b := a
  //a := 1 // assignment overlap with a := 0
  c := a

  val x = Reg(UInt(4 bits))
  val y, z = UInt(4 bits)

  y := x      // y read x with the value 0
  x := x + 1
  z := x

}

object Experiment {
  def main(args: Array[String]) {
    //InOutWrapper
    SpinalVerilog(new Experiment).printPruned()
  }
}
