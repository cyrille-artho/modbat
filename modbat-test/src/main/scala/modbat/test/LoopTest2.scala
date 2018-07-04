package modbat.test

import modbat.dsl._

class LoopTest2 extends Model {
  var i : Int = 0

  // transitions
  "ok" -> "ok2" := skip
  "ok2" -> "ok2" := {
    i = i + 1
    assert (i < 5)
  }
}
