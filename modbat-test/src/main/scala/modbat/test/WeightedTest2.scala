package modbat.test

import modbat.dsl._

class WeightedTest2 extends Model {
  var n = 0

  "init" -> "init" := {
    require(n < 3)
    n = n + 1
  }
  "init" -> "init" := {
    assert(false)
  } weight 0
}
