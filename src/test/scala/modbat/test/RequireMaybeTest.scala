package modbat.test

import modbat.dsl._

class RequireMaybeTest extends Model {
  // transitions
  "ok" -> "err" := {
    require(false)
    maybe {
      System.err.println("Hit maybe!")
      assert (false, { "Hit maybe" })
    } or_else {
      System.err.println("Hit else!")
      assert (false, { "Hit else" } )
    }
    assert(false, { "Code after maybe/else" } )
  }
}
