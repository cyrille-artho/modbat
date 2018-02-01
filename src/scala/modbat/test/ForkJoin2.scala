package modbat.test

import modbat.dsl._

class ForkJoin2 extends Model { // Worker never finishes
  "init" -> "waiting" := {
    join(new ForkJoin2) // target has never been launched
  }
  "waiting" -> "done" := skip
}
