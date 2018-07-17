package modbat.test

import modbat.dsl._

/** Test choice generator for functions. */ 
class Choices extends Model {
  var n = 0
  // transitions
  "ok" -> "ok" := {
    choose(
      { () => n = 1 },
      { () => n = 2 },
      { () => n = 3 }
    )
  }
  "ok" -> "end" := {
    assert (n != 2)
  }
}
