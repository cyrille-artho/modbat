package modbat.test

import modbat.dsl._

class SetWeight2 extends Model {
  var n = 0

  "init" -> "init" := {
    assert(n < 10)
    n += 1
    if(n == 10) {
      setWeight("loop", 0)
      setWeight("end", 0.01)
    }
  } label "loop"

  "init" -> "end" := {
    assert(n == 10)
  } label "end" weight 0
}
