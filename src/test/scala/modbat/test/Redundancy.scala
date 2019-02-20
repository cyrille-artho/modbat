package modbat.test

import modbat.dsl._

class Redundancy extends Model {
  var switch = false
  var count = 0

  // transitions
  "reset" -> "counting" := {
    switch = true
    count = 0
  }
  "counting" -> "counting" := {
    switch = !switch
  }
  "counting" -> "counting" := skip
  "counting" -> "counting" := {
    if (switch) {
      count += 1
    }
  }
  "counting" -> "end" := {
    assert (count != 0)
  }
}
