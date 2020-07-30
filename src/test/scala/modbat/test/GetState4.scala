package modbat.test

import modbat.dsl._

class GetState4 extends Model {
  var i: Int = 0
  @States(Array("state1", "state2")) @Weight(1.0) def invariantCheck: Unit = {
    val state = getCurrentState
    if (i == 1) assert (state.equals("state1"), { "Expected state1 but got " + state })
    else assert (state.equals("state2"), { "Expected state2 but got " + state })
  }

  // transitions
  "reset" -> "err" := {
    // insert code here
    assert(getCurrentState.equals("reset"))
    i = 1
  } nextIf { () => getCurrentState.equals("reset") } -> "state1"
  "state1" -> "state2" := {
    i = 2
  }
  "state2" -> "err" := skip
  "err" -> "err" := { assert (false) }
}
