package modbat.test

import scala.collection.mutable.ListBuffer

import modbat.dsl._

class CarModel extends Model {
  val GEAR_MAX = 4
  val SEQ_MAX_LEN = 5
  val SEQ_MIN_LEN = 3
  var gear = 1
  var power = 0
  val cmdSeq = new ListBuffer[(String, Int)]()

  @Before def init {
    gear = choose(1, GEAR_MAX + 1)
    power = choose(0, 2)
    cmdSeq += (("gear", gear))
    cmdSeq += (("power", power))
  }

  def shiftUp {
    require(gear < GEAR_MAX)
    require(cmdSeq.size < SEQ_MAX_LEN)
    gear = gear + 1
    cmdSeq += (("gear", gear))
  }

  def shiftDown {
    require(gear > 1)
    require(cmdSeq.size < SEQ_MAX_LEN)
    gear = gear - 1
    cmdSeq += (("gear", gear))
  }

  def togglePower {
    require(cmdSeq.size < SEQ_MAX_LEN)
    power = 1 - power
    cmdSeq += (("power", power))
  }

  // transitions
  "init" -> "init" := shiftUp
  "init" -> "init" := shiftDown
  "init" -> "init" := togglePower
  "init" -> "show" := {
    require(cmdSeq.size >= SEQ_MIN_LEN)
    println("Test: " + cmdSeq.mkString("\t"))
  }
}
