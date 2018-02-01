package modbat.test

import modbat.dsl._

class Worker(parent: ForkJoin) extends Model {
  "init" -> "end" := { parent.counter += 1 }
}

class ForkJoin extends Model {
  var counter = 0
  "init" -> "waiting" := {
    val w = new Worker(this)
    launch(w)
    join(w)
  }
  "waiting" -> "done" := {
    assert (counter != 0)
  }
}
