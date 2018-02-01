package modbat.test

import modbat.dsl._

class MaybeElseTest2 extends Model {
  // transitions
  "ok" -> "ok" := skip
  "ok" -> "err" := {
    Console.out.println("before")
    maybe {
      assert (false)
    } or_else {
      assert (true)
    }
    Console.out.println("after")
  }
}
