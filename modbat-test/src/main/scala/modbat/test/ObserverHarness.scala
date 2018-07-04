package modbat.test

import modbat.dsl._

class ObserverHarness extends CounterModel with Model {
  // transitions
  "reset" -> "counting" := {
    launch(new TestObserver(this))
  }
  "counting" -> "counting" := {
    i = i + 1
  }
  "counting" -> "end" := skip
}
