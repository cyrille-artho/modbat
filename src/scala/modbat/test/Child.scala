package modbat.test

import modbat.dsl._

class Child(var id: Int) extends Model {
  // transitions
  "init" -> "running" := skip
  "running" -> "running" := {
    id = id + 1
  }
  "running" -> "done" := {
    assert (id < 5)
  }
}
