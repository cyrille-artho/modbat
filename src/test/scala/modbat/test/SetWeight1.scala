package modbat.test

import modbat.dsl._

class SetWeight1 extends Model {
  "init" -> "mid" := {
//    setWeight("end", 0)
  } label "init"

  "mid" -> "end" := {
    assert(false)
  } label "end"
}

