package modbat.test

import modbat.dsl._

class NoSelfJoin extends Model {
  "stuck" -> "stuck" := {
    join(this)
  }
}
