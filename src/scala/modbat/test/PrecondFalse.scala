package modbat.test

import modbat.dsl._

class PrecondFalse extends Model {
  "ok" -> "err" := {
    require (false)
  }
  "err" -> "err" := {
    assert (false)
  }
}
