package modbat.test

import modbat.dsl._

object Child4 {
  @Init def init { System.out.println("!!!child-init: should not be executed!") }
  @Shutdown def shutdown { System.out.println("!!!child-shutdown: should not be executed!") }

  @Before def child_companion_before {
    System.out.println("child-companion:before")
  }

  @After def child_companion_after {
    System.out.println("child-companion:after")
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
