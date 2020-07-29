package modbat.test

import modbat.dsl._

class InvCheck3 extends Model {
  var i: Int = 0
  @States(Array("somestate", "state2")) @Weight(1.0) def inc: Unit = { i = i + 1 }

  @States(Array("end")) def check: Unit = { assert (i == 0, { "i = " + i }) }

  // transitions
  "reset" -> "somestate" := skip
  "somestate" -> "state2" := skip
  "state2" -> "end" := skip
  "reset" -> "end" := skip
}
