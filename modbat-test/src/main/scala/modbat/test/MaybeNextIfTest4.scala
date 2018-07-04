package modbat.test

import modbat.dsl._

class MaybeNextIfTest4 extends Model {
  // transitions
  "ok" -> "err1" := {
    Console.out.println("ok")
  } maybeNextIf {() => true } -> "err2"
  "err1" -> "err1" := {
    Console.out.println("err1")
    assert (false)
  }
  "err2" -> "err2" := {
    Console.out.println("err2")
    assert (false)
  }
}
