package modbat.test

import modbat.dsl._

class ObserverHarness3 extends CounterModel with Model {
  // transitions
  "reset" -> "reset" := {
    launch(new TestObserver2(this))
  }
  "reset" -> "counting" := skip
  "counting" -> "counting" := {
    i = i + 1
  } weight 2
  "counting" -> "end" := skip
}
