package modbat.examples

import modbat.dsl._

class SimpleRandomModel extends Model {
  var counter: SimpleCounter = _

  // transitions
  "reset" -> "zero" := {
    counter = new SimpleCounter()
  }
  "zero" -> "one" := {
    counter.inc
  }
  "one" -> "random" := randomSearch("modbat.examples.SimpleCounter")

}