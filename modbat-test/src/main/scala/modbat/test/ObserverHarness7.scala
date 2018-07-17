package modbat.test

import modbat.dsl._

class ObserverHarness7 extends CounterModel with Model {
  // transitions
  "reset" -> "counting" := {
    launch(new ConcurrentObserver(this))
  }
  "counting" -> "counting" := {
    i = i + 1
  }
  "counting" -> "end" := skip
}
