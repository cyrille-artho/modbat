package modbat.test

import modbat.dsl._

class RequireMaybeTest extends Model {
  // transitions
  "ok" -> "err" := {
    require(false)
    maybe {
      Console.err.println("Hit maybe!")
      assert (false, { "Hit maybe" })
    } or_else {
      Console.err.println("Hit else!")
      assert (false, { "Hit else" } )
    }
    assert(false, { "Code after maybe/else" } )
  }
}
