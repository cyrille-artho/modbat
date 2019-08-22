package modbat.test

import modbat.dsl._

class SingletonTest extends Model {
  @Before def start() {
    System.out.println("start")
  }

  @After def end() {
    System.out.println("end")
  }

  // transitions
  "reset" -> "end" := {
  }
}
