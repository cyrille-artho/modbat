package modbat.test

import modbat.dsl._

class MaybeNextIfTest5 extends Model {
  // transitions
  "ok" -> "err1" := {
    Console.out.println("ok")
  } maybeNextIf {() => !MockEnv.nonDetCall} -> "err2"
  "err1" -> "err1" := {
    Console.out.println("err1")
    assert (false)
  }
  "err2" -> "err2" := {
    Console.out.println("err2")
    assert (false)
  }
}
