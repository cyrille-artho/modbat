package modbat.test

import modbat.dsl._

class SetWeight2 extends Model {
  var n = 0

  "init" -> "init" := {
    assert(n == 0)
//    setWeight("loop", 0)
//    setWeight("end", 1)
    n += 1
  } label "loop"

  "init" -> "end" := {
    assert(n == 1)
  } label "end" weight 0
}
