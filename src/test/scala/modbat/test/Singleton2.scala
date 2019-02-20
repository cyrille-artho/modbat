package modbat.test

import modbat.dsl._

object Singleton2 {
  @Init def init() {
    println("init")
  }

  @Shutdown def shutdown() {
    println("shutdown")
  }
}

class Singleton2 extends Model {
  @Before def start() {
    println("start")
  }

  @After def end() {
    println("end")
  }

  // transitions
  "reset" -> "end" := {
    println("action")
    assert(false)
  }
}
