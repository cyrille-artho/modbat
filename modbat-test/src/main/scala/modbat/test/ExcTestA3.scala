package modbat.test

import modbat.dsl._

class ExcTestA3 extends Model {
  // transitions
  "ok" -> "err" := {
    MockExcEnv2.nonDetCall
  } throws ("IOException")
  "err" -> "err" := {
    assert (false)
  }
}
