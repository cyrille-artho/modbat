package modbat.test

import modbat.dsl._

class Choose00 extends Model {
  "init" -> "end" := {
    assert(choose(0, 0) == 0)
  }
}
