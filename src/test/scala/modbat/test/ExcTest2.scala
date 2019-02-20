package modbat.test

import modbat.dsl._

class ExcTest2 extends Model {
  // transitions
  "ok" -> "ok" := {
    MockExcEnv2.nonDetCall
  } catches ("IOException" -> "err")
  "err" -> "err" := {
    assert (false)
  }
}
