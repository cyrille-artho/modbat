package modbat.test

import modbat.dsl._

class MaybeElseTest2 extends Model {
  // transitions
  "ok" -> "ok" := skip
  "ok" -> "err" := {
    System.out.println("before")
    maybe {
      assert (false)
    } or_else {
      assert (true)
    }
    System.out.println("after")
  }
}
