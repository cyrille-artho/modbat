package modbat.test

import modbat.dsl._

class ExcTest3 extends Model {
  // transitions
  "ok" -> "ok" := {
    if (MockEnv.nonDetCall) { // true if online, false offline
      throw new Exception("Test...")
    }
    MockExcEnv2.nonDetCall
  } catches ("IOException" -> "err2", "Exception" -> "err1")
  "err1" -> "err1" := {
    Console.out.println("err1")
    assert (false)
  }
  "err2" -> "err2" := {
    Console.out.println("err2")
    assert (false)
  }
}
