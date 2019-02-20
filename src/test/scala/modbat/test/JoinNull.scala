package modbat.test

import modbat.dsl._

class JoinNull extends Model {
  "init" -> "end" := {
    join(null)
  }
}
