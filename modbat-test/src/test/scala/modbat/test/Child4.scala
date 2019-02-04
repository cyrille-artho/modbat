package modbat.test

import modbat.dsl._

object Child4 {
  @Init def init { println("!!!child-init: should not be executed!") }
  @Shutdown def shutdown { println("!!!child-shutdown: should not be executed!") }

  @Before def child_companion_before {
    println("child-companion:before")
  }

  @After def child_companion_after {
    println("child-companion:after")
  }
}

class Child4(var id: Int) extends Model {
  // transitions
  "init" -> "running" := skip
  "running" -> "running" := {
    id = id + 1
  }
  "running" -> "done" := {
    assert (id < 5)
  }
}
