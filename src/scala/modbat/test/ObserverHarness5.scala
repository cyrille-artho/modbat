package modbat.test

import modbat.dsl._

class ObserverHarness5 extends CounterModel with Model {
  // transitions
  "reset" -> "counting" := {
    launch(new TestObserver5(this))
  }
  "counting" -> "counting" := {
    i = i + 1
  }
  "counting" -> "end" := skip
}
