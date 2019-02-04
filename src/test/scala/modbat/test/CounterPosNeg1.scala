package modbat.test

import modbat.examples.Counter
import modbat.dsl._

/* A modified sign abstraction (saturating counter w/ sign).
 * Used for testing longer paths where the value goes to -3. */
// TODO: Support @States or @inv annotation of methods for
// invariants that hold always or in certain states, to
// eliminate copy/paste of assertions.
class CounterPosNeg1 extends Model {
  val counter: Counter = new Counter()

  // transitions
  "zero" -> "pos1" := {
    counter.inc(1)
  }
  "pos1" -> "pos" := {
    counter.inc(1)
  }
  "pos1" -> "zero" := {
    counter.inc(-1)
  } label "dec"
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
  "zero" -> "neg1" := {
    counter.inc(-1)
  } label "dec"
  "neg1" -> "neg" := {
    counter.inc(-1)
    assert (counter.value > -3)
  } label "dec"
  "neg1" -> "zero" := {
    counter.inc(1)
  }
  "neg" -> "any" := {
    counter.inc(1)
  }
  "neg" -> "neg" := {
    counter.inc(-1)
    assert (counter.value > -3)
  } label "dec"
}
