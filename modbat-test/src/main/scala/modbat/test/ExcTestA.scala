package modbat.test

import modbat.dsl._

class ExcTestA extends Model {
  // transitions
  "ok" -> "err" := {
    true
  } throws ("IOException")
  "err" -> "err" := {
    assert (false)
  }
}
