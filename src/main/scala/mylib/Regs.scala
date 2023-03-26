package mylib

import spinal.core._
import spinal.core.sim._

import scala.collection.mutable

case class SimpleReg(name:String, size:Int=4,desc:String=""){}

class RegMem(bitcount:BitCount,size:Int) extends Area{
  val data = Mem(Bits(bitcount),size).init(Array.fill(size)(B(0)))
  //val data = Vec(Reg(Bits(bitcount)).init(0),size)

  val outRange = Bool

  outRange := False

  val regsMap = mutable.LinkedHashMap[Int,SimpleReg]()

  def add(addr:Int,name:String,desc:String): Unit ={
    regsMap += ( addr-> SimpleReg(name,bitcount.value/8,desc))
  }

  val decodeMap = mutable.Map[(Component,UInt), UInt]()
  val accessMap = mutable.Map[(Component,UInt), Bits]()

  Component.current.addPrePopTask(() => {
    assert(regsMap.size <= size)
    for( (addr,reg) <- regsMap){
      println(f"offset:0x$addr%x  name:${reg.name}  desc:${reg.desc}")
    }
  })

  def decode(in:Int):Int={
    for( ((addr,_),index) <- regsMap.zipWithIndex){
      if(in==addr){
        return index
      }
    }
    throw new Exception("didn't find this addr in regs!")
  }

  def decode(in:UInt):UInt={
    val key = (Component.current,in)
    if(decodeMap.contains(key)){return decodeMap(key)}  // check whether we have decoded this addr variable before
    val out = UInt(log2Up(size) bits)
    switch(in){
      for( ((addr,_),i) <- regsMap.zipWithIndex){
        is(addr){
          out := i
        }
      }
      default{
        outRange := True
        out:=0
      }
    }
    decodeMap += key->out
    out
  }

  def apply(addr:Int)=data(U(decode(addr)).resized)
  def apply(addr:UInt):Bits={
    val index = decode(addr)
    val key = (Component.current,index)
    if(accessMap.contains(key)){return accessMap(key)}

    // start to create access mux for this addr(decoded)
    // and we will save the mux in the accessMap, so that we can use it next time
    // refered the implement of Vec
    val origin = data(index)
    val ret = cloneOf(origin)
    ret := origin
    ret.compositeAssign =new Assignable {
      override  def assignFromImpl(that: AnyRef, target: AnyRef, kind: AnyRef) = {
        data(index) := that.asInstanceOf[Bits]
      }
      override def getRealSourceNoRec: Any = RegMem.this
    }
    accessMap += key->ret
    ret
  }

}

class RegMemTest extends Component{
  val regs = new RegMem(16 bits,16)
  regs.add(0x00,"period","period of the counter")
  regs.add(0x01,"CCR1",desc="ccr of channel 1")
  regs.add(0x02,"CCR2",desc="ccr of channel 2")

  regs.add(addr=0x40,"TEST",desc="TEST")

  regs(0x40) := 0xabcd

  val data1: Bits = regs(0x40)
  val data2: Bits = regs(U(0x40)) simPublic()

  val d = U(0x40)

  val data3 = regs(d)
  val data4 = regs(d)

  assert(data3==data4)   // data3 should be the same with data4, as we use a map inside applay()

}

object RegMem_DutTests {
  def main(args: Array[String]): Unit = {
    val compile = SimConfig.withWave.withVerilator.compile {
      val a = new RegMemTest
      a.data1.simPublic()
      a.data2.simPublic()
      a
    }

    compile.doSim("decode int"){
      dut=>
        assert(dut.regs.decode(0x00) == 0x00)
        assert(dut.regs.decode(0x01) == 0x01)
        assert(dut.regs.decode(0x40) == 0x03)

        assert(dut.data1.toInt == dut.data2.toInt)
    }
  }

}