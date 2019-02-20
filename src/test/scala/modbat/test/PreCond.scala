package modbat.test

import modbat.dsl._

class PreCond extends Model {
  var i = 0

  // transitions
  "init" -> "end" := {
        require (i > 0, { "i = " + 1 })
  }
  "init" -> "init" := {
    maybe (i = i + 1)
  }
}
