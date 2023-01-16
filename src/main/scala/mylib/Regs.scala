package mylib

import spinal.core._
import scala.collection.mutable.Map

case class SimpleReg(name:String, size:Int=4,desc:String=""){

}

class RegArea(desc_data:Map[Int,SimpleReg]) extends Area{

  val data=Map[Int,Bits]()
  var max_size:Int=0
  for( (addr,simplereg) <- desc_data){
    if(simplereg.size > max_size) {
      max_size = simplereg.size
    }
    val temp = Reg(Bits( (simplereg.size * 8)bits))
    data += (addr-> temp)
    valCallbackRec(temp,simplereg.name)
  }

  def write(addr:UInt,value:Bits){}

  def write(addr:Int, value:Bits)={
    data(addr) := value
  }

  def read(read_addr:UInt)={
    val temp = Bits( (max_size * 8) bits)
    switch(read_addr){
      for( (addr,reg) <- data){
        is(addr){
          temp:= reg
        }
      }
      if(1<<read_addr.getBitsWidth != data.size){
        default{
          temp := B(0)
        }
      }
    }
    temp
  }

  def get(addr:Int) = data(addr)


}

class Regs {
  var data: Map[Int, SimpleReg] =Map[Int,SimpleReg]()

  def add(simplereg : SimpleReg,addr:Int) = data+=(addr->simplereg)

  def add(name:String,addr:Int,size:Int=4,desc:String=""): Unit ={
    add(SimpleReg(name,size,desc),addr)
  }

  def count: Int = data.size
  def buildArea()=new RegArea(data)


}
