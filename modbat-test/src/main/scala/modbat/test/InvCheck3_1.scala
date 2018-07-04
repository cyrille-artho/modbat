package modbat.test

import modbat.dsl._

class InvCheck3_1(var i: Int) extends Model {
  def this() = this(0)

  @States(Array("somestate", "state2")) @Weight(1.5) def inc { i = i + 1 }

  @States(Array("end")) def check { assert (i == 0, { "i = " + i }) }

  // transitions
  "reset" -> "somestate" := skip
  "somestate" -> "state2" := skip
  "state2" -> "end" := skip
  "reset" -> "end" := skip
}
