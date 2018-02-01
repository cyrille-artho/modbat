package modbat.test

import modbat.dsl._

class NextIfTest2 extends Model {
  // transitions
  "ok" -> "ok2" := {} nextIf {() => false} -> "err"
  "ok2" -> "err" := skip
  "err" -> "err" := {
    assert (false)
  }
}
