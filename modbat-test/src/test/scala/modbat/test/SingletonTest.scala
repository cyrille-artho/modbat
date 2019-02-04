package modbat.test

import modbat.dsl._

class SingletonTest extends Model {
  @Before def start() {
    println("start")
  }

  @After def end() {
    println("end")
  }

  // transitions
  "reset" -> "end" := {
  }
}
