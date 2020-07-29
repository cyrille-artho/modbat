package modbat.test

import modbat.dsl._

class IllArgTest extends Model {
  def illArg(): Unit = {
    throw new IllegalArgumentException("not from failed require")
  }

  // transitions
  "ok" -> "err" := {
    require(true)
    illArg
  }
}
