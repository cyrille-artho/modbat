package modbat.test

import modbat.dsl._

class ExcTestA4 extends Model {
  // transitions
  "ok" -> "err" := {
    throw new java.io.IOException("Test")
    true
  } throws ("IOException")
  "err" -> "err" := {
    assert (false)
  }
}
