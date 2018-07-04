package modbat.test

import modbat.dsl._

class NextIfTest4 extends Model {
  // transitions
  "ok" -> "ok2" := {} nextIf {() => true} -> "err"
  "ok2" -> "err" := skip
  "err" -> "err" := {
    assert (false)
  }
}
