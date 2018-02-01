package modbat.test

import modbat.dsl._

class ChooseBool extends Model {
  "init" -> "end" := {
    assert(choose())
  }
}
