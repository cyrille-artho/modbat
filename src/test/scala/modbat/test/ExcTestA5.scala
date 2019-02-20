package modbat.test

import modbat.dsl._

class ExcTestA5 extends Model {
  // transitions
  "ok" -> "ok" := {
    if (MockEnv.nonDetCall) { // true if online, false offline
      throw new java.io.IOException("Online...")
    }
    throw new Exception("Offline...")
    true
  } catches ("IOException" -> "err1", "Exception" -> "err2")
  "err1" -> "err1" := {
    Console.out.println("err1")
    assert (false)
  }
  "err2" -> "err2" := {
    Console.out.println("err2")
    assert (false)
  }
}
