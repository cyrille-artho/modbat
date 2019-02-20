package modbat.test

import modbat.dsl._

class WeightedTest extends Model {
  var n = 0

  "init" -> "init" := {
    n = n + 1
    assert (n < 10, { "n = " + n } )
  } weight 2
  "init" -> "init" := {
    n = n - 2
  }
}
