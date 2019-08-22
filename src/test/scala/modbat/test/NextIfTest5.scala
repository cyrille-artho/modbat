package modbat.test

import modbat.dsl._

class NextIfTest5 extends Model {
  // transitions
  "ok" -> "ok" := {} nextIf ({() => MockEnv.nonDetCall} -> "err1",
			     {() => !MockEnv.nonDetCall} -> "err2")
  "err1" -> "err1" := {
    System.out.println("err1")
    assert (false)
  }
  "err2" -> "err2" := {
    System.out.println("err2")
    assert (false)
  }
}
