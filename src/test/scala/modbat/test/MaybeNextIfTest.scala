package modbat.test

import modbat.dsl._

class MaybeNextIfTest extends Model {
  // transitions
  "ok" -> "ok" := {
    System.out.println("ok")
  } maybeNextIf {() => MockEnv.nonDetCall} -> "err"
  "err" -> "err" := {
    assert (false)
  }
}
