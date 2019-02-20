package modbat.test

import modbat.dsl._

class Immediate extends Model {
  "init" -> "init" := {
    launch(new Catches)
  }
}
