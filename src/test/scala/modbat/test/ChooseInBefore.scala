package modbat.test

import modbat.dsl._

class ChooseInBefore extends Model {
  var n = 0

  @Before def init: Unit = {
    n = choose(1, 3)
  }

  // transitions
  "init" -> "end" := {
    assert(n == 1, "n = " + n)
  }
}

