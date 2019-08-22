package modbat.test

import modbat.dsl._

class RequireMaybeTest2 extends Model {
  def maybeTest() {
    maybe {
      System.err.println("Hit maybe!")
      assert (false, { "Hit maybe" })
    } or_else {
      System.err.println("Hit else!")
      assert (false, { "Hit else" } )
    }
  }

  // transitions
  "ok" -> "err" := {
    require(false)
    maybeTest()
  }
}
