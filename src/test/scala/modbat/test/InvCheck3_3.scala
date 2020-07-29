package modbat.test

import modbat.dsl._

/* Variant of 3_1 with trace annotation on field. */
class InvCheck3_3 extends Model {
  @Trace var i = 0

  @States(Array("state1", "state2")) @Weight(1.5) def inc: Unit = { i = i + 1 }

  @States(Array("end")) def check: Unit = { assert (i == 0, { "i = " + i }) }

  // transitions
  "state1" -> "state2" := skip
  "state2" -> "end" := skip
}
