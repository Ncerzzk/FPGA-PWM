package mylib
import spinal.core._
import spinal.lib.bus.amba3.apb.Apb3
import spinal.lib.sim._

object utils {

  def outsideCondScope[T](that : => T) : T = {
    val body = Component.current.dslBody  // Get the head of the current component symboles tree (AST in other words)
    val ctx = body.push()                           // Now all access to the SpinalHDL API will be append to it (instead of the current context)
    val swapContext = body.swap()         // Empty the symbole tree (but keep a reference to the old content)
    val ret = that                        // Execute the block of code (will be added to the recently empty body)
    ctx.restore()                            // Restore the original context in which this function was called
    swapContext.appendBack()              // append the original symboles tree to the modified body
    ret                                   // return the value returned by that
  }
}

class Sequencer extends Area {
  private var steps = 0
  val step_counter = utils.outsideCondScope{
    Reg(UInt()) init 0
  }.setPartialName("step_count")
  def addStep(gate: =>Bool) = {
    when(step_counter === steps) {
      when(gate) { step_counter := steps + 1 }
    }
    steps = steps + 1
    this
  }

  Component.current.addPrePopTask(() => {
    step_counter.setWidth(log2Up(steps+1))
    when(step_counter === steps){
      step_counter.clearAll()
    }
  })
}

class Gowin_OSC extends BlackBox{
  val io = new Bundle {
    val oscout = out Bool()
  }
  addRTLPath("gowin_ip/Gowin_OSC.v")
  noIoPrefix()
}

class OSCH extends BlackBox{
  val io = new Bundle{
    val oscout = out Bool()
  }
  noIoPrefix()
}

class APB3OperationArea(apb_m:Apb3) extends Area{
  import APB3Phase._
  val phase = RegInit(SETUP) //optimization for faster apb access
  val transfer = False
  var writeOkMap=Map[(Component,UInt),Bool]()
  apb_m.PADDR.setAsReg()
  apb_m.PWRITE.setAsReg()

  apb_m.PENABLE := False
  apb_m.PWDATA := B(0)
  apb_m.PWRITE.rise()
  def write_t(addr:UInt,value:Bits)(block: => Unit = Unit):Bool={
    apb_m.PWRITE := True
    apb_m.PADDR := addr.resized
    apb_m.PWDATA := value
    transfer := True

    when(apb_m.PENABLE){
      transfer := False
      block
    }
    //    val key = (Component.current ,addr)
    //    if(writeOkMap.contains(key)){
    //      return writeOkMap(key)
    //    }

    val ret = apb_m.PENABLE
    ret
  }

  def write_t_withoutcallback(addr:UInt,value:Bits):Bool={
    write_t(addr,value){}
  }

  def read_t(addr:UInt)(block: Bits => Unit) ={
    apb_m.PWRITE := False
    apb_m.PADDR := addr.resized
    transfer := True
    when(apb_m.PENABLE){
      transfer := False
      block(apb_m.PRDATA)
    }
    apb_m.PENABLE
  }

  switch(phase){
    is(IDLE){
      apb_m.PSEL:= B(0)
      apb_m.PENABLE := False
      when(transfer){
        phase := SETUP
      }
    }

    is(SETUP){
      apb_m.PSEL:=B(1)
      when(transfer){
        phase:= ACCESS
      }
    }

    is(ACCESS){
      apb_m.PSEL:=B(1)
      apb_m.PENABLE := True
      phase:= SETUP
    }
  }
}