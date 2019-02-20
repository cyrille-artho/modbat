package modbat.examples

import modbat.dsl._

class CounterModel2 extends Model {
  var counter: Counter = _

  // transitions
  "reset" -> "zero" := {
    counter = new Counter()
  }
  "zero" -> "zero" := {
    counter.toggleSwitch
  } label "toggle"
  "zero" -> "one" := {
    counter.inc(1)
  } label "inc1"
  "one" -> "two" := {
    counter.inc(1)
  } label "inc1"
  "zero" -> "two" := {
    counter.inc(2)
  } label "inc2"
  "two" -> "end" := {
    assert (counter.value == 2)
  } label "assert"
}
