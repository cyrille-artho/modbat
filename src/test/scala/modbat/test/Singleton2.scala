package modbat.test

import modbat.dsl._

object Singleton2 {
  @Init def init(): Unit = {
    println("init")
  }

  @Shutdown def shutdown(): Unit = {
    println("shutdown")
  }
}

class Singleton2 extends Model {
  @Before def start(): Unit = {
    println("start")
  }

  @After def end(): Unit = {
    println("end")
  }

  // transitions
  "reset" -> "end" := {
    println("action")
    assert(false)
  }
}
