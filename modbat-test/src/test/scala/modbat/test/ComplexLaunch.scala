package modbat.test

import modbat.dsl._

class ComplexLaunch extends Model {
  var id = 0
  // transitions
  "init" -> "active" := skip
  "active" -> "active" := {
    id = id + 1
    launch(new Child(id))
  }
  "active" -> "done" := {
    assert (id < 3)
  }
}
