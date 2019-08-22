package modbat.test

import modbat.dsl._

class NextIfTest3 extends Model {
  // transitions
  "ok" -> "err1" := {} nextIf {() => !MockEnv.nonDetCall} -> "err2"
  "err1" -> "err1" := {
    System.out.println("err1")
    assert (false)
  }
  "err2" -> "err2" := {
    System.out.println("err2")
    assert (false)
  }
}
