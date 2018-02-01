package modbat.examples

import modbat.dsl._

/* A small model for the counter using a sign abstraction.
 * Used for testing longer paths where the value goes to -3. */
// TODO: Support @States or @inv annotation of methods for
// invariants that hold always or in certain states, to
// eliminate copy/paste of assertions.
class CounterPosNeg extends Model {
  val counter: Counter = new Counter()

  // transitions
  "zero" -> "pos" := {
    counter.inc(1)
  }
  "pos" -> "pos" := {
    counter.inc(1)
  }
  "pos" -> "any" := {
    counter.inc(-1)
  } label "dec"
  "any" -> "any" := {
    counter.inc(1)
  }
  "any" -> "any" := {
    counter.inc(-1)
    assert (counter.value > -3)
  } label "dec"
  "zero" -> "neg" := {
    counter.inc(-1)
  } label "dec"
  "neg" -> "neg" := {
    counter.inc(-1)
    assert (counter.value > -3)
  } label "dec"
  "neg" -> "any" := {
    counter.inc(1)
  }
}
