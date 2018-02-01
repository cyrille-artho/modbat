package modbat.test

import modbat.dsl._

object Catches {
  var n = 0
  var active = -1
}

class Catches extends Model {
  import Catches.{n,active}
  n += 1
  val id = n

  // transitions
  "ok" -> "ok" := {
    active = id
    throw new Exception("Test...")
    true
  } catches ("Exception" -> "err")
  "err" -> "ok" := {
    assert (active == id)
  }
}
