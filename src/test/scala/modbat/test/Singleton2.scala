package modbat.test

import modbat.dsl._

object Singleton2 {
  @Init def init() {
    System.out.println("init")
  }

  @Shutdown def shutdown() {
    System.out.println("shutdown")
  }
}

class Singleton2 extends Model {
  @Before def start() {
    System.out.println("start")
  }

  @After def end() {
    System.out.println("end")
  }

  // transitions
  "reset" -> "end" := {
    System.out.println("action")
    assert(false)
  }
}
