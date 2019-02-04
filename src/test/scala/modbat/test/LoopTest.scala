package modbat.test

import modbat.dsl._

class LoopTest extends Model {
  var i : Int = 0

  // transitions
  "ok" -> "ok" := {
    i = i + 1
    assert (i < 5)
  }
}
