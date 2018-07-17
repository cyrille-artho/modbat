package modbat.test

import modbat.dsl._

class InvalidMethod extends Model {
  @States(Array("somestate", "x")) def invariantCheck(i: Int) {
    assert (i != 0, { "i = " + i })
  }

  @States(Array("somestate", "x")) def invariantCheck() {
    maybe(assert(false))
  }

  // transitions
  "reset" -> "somestate" := skip
  "somestate" -> "end" := skip
  "reset" -> "end" := skip
}
