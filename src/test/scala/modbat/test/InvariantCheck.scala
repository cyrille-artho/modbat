package modbat.test

import modbat.dsl._

class InvariantCheck extends Model {
  var i: Int = 0
  @States(Array("somestate", "state2")) @Weight(1.0) def invariantCheck: Unit = {
    assert (i != 0, { "i = " + i })
  }

  // transitions
  "reset" -> "somestate" := {
    // insert code here
    maybe (i = 1)
  }
  "somestate" -> "state2" := {
    maybe (i = 2)
  }
  "state2" -> "end" := skip
  "reset" -> "end" := skip
}
