package modbat.test

import modbat.dsl._

class ExcTest extends Model {
  // transitions
  "ok" -> "ok" := {
    MockExcEnv.nonDetCall
  } catches ("IOException" -> "err")
  "err" -> "err" := {
    assert (false)
  }
}
