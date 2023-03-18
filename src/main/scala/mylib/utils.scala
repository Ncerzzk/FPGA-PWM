package mylib
import spinal.core._
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

  val finished = outsideCondScope {
    RegInit(True).setWeakName("finished")
  }

  def after(block1: => Bool)(block2: Bool => Unit): Unit ={
    val finished = RegInit(False)
    when(finished){
      when(block1){finished:= False}
    }otherwise{
      block2(finished)   // we should set finished to True in block2
    }
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