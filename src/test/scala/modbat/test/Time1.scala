package modbat.test

import modbat.dsl._

class Time1() extends Model {
  "init" -> "mid" := {
  } stay (1000,2000)
}

