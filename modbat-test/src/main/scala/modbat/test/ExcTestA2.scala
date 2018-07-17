package modbat.test

import modbat.dsl._

class ExcTestA2 extends Model {
  // transitions
  "ok" -> "ok" := {
    MockExcEnv.nonDetCall
  } throws ("IOException")
  "err" -> "err" := {
    assert (false)
  }
}
