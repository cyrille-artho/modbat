package modbat.test

import modbat.dsl._

class SingletonTest extends Model {
  @Before def start(): Unit = {
    println("start")
  }

  @After def end(): Unit = {
    println("end")
  }

  // transitions
  "reset" -> "end" := {
  }
}
