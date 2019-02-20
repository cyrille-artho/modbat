package modbat.test

import modbat.dsl._

class NextIfTest extends Model {
  // transitions
  "ok" -> "ok" := {} nextIf {() => MockEnv.nonDetCall} -> "err"
  "err" -> "err" := {
    assert (false)
  }
}
