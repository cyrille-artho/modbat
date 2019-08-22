package modbat.test

import modbat.dsl._

class TickTockTest extends Model {
  var i = 0
  // transitions
  "reset" -> "counting" := {
    launch(new TickTockObserver(this))
  }
  "counting" -> "counting" := {
    val j = choose(1, 5)
    System.out.println(j)
    i = i + j
  }
  "counting" -> "end" := {
    assert (i < 10)
  }
}
