package modbat.test

import modbat.dsl._

class Choose00 extends Model {
  "init" -> "end" := {
    choose(0, 0)
  }
}
