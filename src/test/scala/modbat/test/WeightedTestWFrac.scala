package modbat.test

import modbat.dsl._

class WeightedTestWFrac extends Model {
  var n = 0

  "init" -> "init" := {
    n = n + 1
    assert (n < 10, { "n = " + n } )
  }
  "init" -> "init" := {
    n = n - 2
  } weight 0.5
}
