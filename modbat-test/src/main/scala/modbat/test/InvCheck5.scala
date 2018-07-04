package modbat.test

import modbat.dsl._

class InvCheck5 extends Model {
  var i: Int = 0
  @States(Array("somestate", "state2"))
  @Throws(Array("AssertionError"))
  @Weight(1.0)
  def invariantCheck {
    assert (i != 0, { "i = " + i })
  }

  // transitions
  "reset" -> "somestate" := {
    // insert code here
    maybe (i = 1)
  } nextIf { () => (i == 0) } -> "state2"
  "somestate" -> "state2" := {
    maybe (i = 2)
  }
  "state2" -> "end" := skip
  "reset" -> "end" := skip
}
