package modbat.test

import modbat.dsl._

class MaybeElseTest extends Model {
  // transitions
  "ok" -> "ok" := skip
  "ok" -> "err" := {
    System.out.println("before")
    maybe {
      assert (true)
    } or_else {
      assert (false)
    }
    System.out.println("after")
  }
}
