package modbat.test

import modbat.dsl._

object CrashSoon {
  var n = 0
}

class CrashSoon extends Model {
  import CrashSoon.n
  "init" -> "end" := {
    n = n + 1
    assert (n < 3)
  }
}
