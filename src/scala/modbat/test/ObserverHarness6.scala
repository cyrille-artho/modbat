package modbat.test

import modbat.dsl._

class ObserverHarness6 extends CounterModel with Model {
  // transitions
  "reset" -> "counting" := {
    launch(new TestObserver6(this))
  }
  "counting" -> "counting" := {
    i = i + 1
  }
  "counting" -> "end" := skip
}
