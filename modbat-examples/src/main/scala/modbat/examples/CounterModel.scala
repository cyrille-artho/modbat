package modbat.examples

import modbat.dsl._

class CounterModel extends Model {
  var counter: Counter = _

  // transitions
  "reset" -> "zero" := {
    counter = new Counter()
  }
  "zero" -> "zero" := {
    counter.toggleSwitch
  }
  "zero" -> "one" := {
    counter.inc(1)
  }
  "one" -> "two" := {
    counter.inc(1)
  }
  "zero" -> "two" := {
    counter.inc(2)
  }
  "two" -> "end" := {
    assert (counter.value == 2)
  }
}
