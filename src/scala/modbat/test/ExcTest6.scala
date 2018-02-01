package modbat.test

import modbat.dsl._

class ExcTest6 extends Model {
  // transitions
  "ok" -> "ok" := {
    throw new Exception("Test...")
    true
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
