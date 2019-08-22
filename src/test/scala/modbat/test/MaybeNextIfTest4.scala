package modbat.test

import modbat.dsl._

class MaybeNextIfTest4 extends Model {
  // transitions
  "ok" -> "err1" := {
    System.out.println("ok")
  } maybeNextIf {() => true } -> "err2"
  "err1" -> "err1" := {
    System.out.println("err1")
    assert (false)
  }
  "err2" -> "err2" := {
    System.out.println("err2")
    assert (false)
  }
}
