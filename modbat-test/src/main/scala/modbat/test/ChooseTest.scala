package modbat.test

import modbat.dsl._

/** Test choice generators and preconditions for offline mode.
    The precondition is likely to fail, so an incorrectly saved
    RNG state in a trace may trigger it incorrectly. The RNG
    state is tested again by an assertion that most likely passes. */
class ChooseTest extends Model {
  // transitions
  "ok" -> "ok" := skip
  "ok" -> "end" := skip
  "ok" -> "err" := {
    require(choose(0, 10) == 0)
    assert(choose(0, 10) != 0)
  }
}
