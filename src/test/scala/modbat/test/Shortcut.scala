package modbat.test

import modbat.dsl._

/* Always fails, but subtrace
   zero -> one -> two can be shortened to zero -> two */
class Shortcut extends Model {
  var count = 0

  // transitions
  "reset" -> "zero" := {
    count = 0
  }
  "zero" -> "one" := {
    count += 1
  }
  "one" -> "two" := {
    count += 1
  }
  "zero" -> "two" := {
    count += 2
  }
  "two" -> "end" := {
    assert (count != 2)
  }
}
