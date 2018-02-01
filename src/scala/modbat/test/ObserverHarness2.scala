package modbat.test

import modbat.dsl._

class ObserverHarness2 extends CounterModel with Model {
  // transitions
  "reset" -> "reset" := {
    launch(new TestObserver(this))
  }
  "reset" -> "counting" := skip
  "counting" -> "counting" := {
    i = i + 1
  } weight 2
  "counting" -> "end" := skip
}
