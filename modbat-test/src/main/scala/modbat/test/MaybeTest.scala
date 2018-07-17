package modbat.test

import modbat.dsl._

class MaybeTest extends Model {
  // transitions
  "ok" -> "ok" := skip
  "ok" -> "err" := {
    maybe {
      assert (false)
    }
  }
}
