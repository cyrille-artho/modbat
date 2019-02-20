package modbat.test

import modbat.dsl._

class PrecondOffline extends Model {
  // transitions
  "ok" -> "err" := {
    require (MockEnv.nonDetCall)
  }
  "err" -> "err" := {
    assert (false)
  }
}
