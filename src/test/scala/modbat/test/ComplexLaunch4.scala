package modbat.test

import modbat.dsl._

object ComplexLaunch4 {
  @Init def init { System.out.println("init") }
  @Shutdown def shutdown { System.out.println("shutdown") }

  @Before def companion_before { System.out.println("companion-before") }
  @After def companion_after { System.out.println("companion-after") }
}

class ComplexLaunch4 (var id: Int) extends Model {
  def this() = this(0)

  // transitions
  "init" -> "active" := skip
  "active" -> "active" := {
    id = id + 1
    launch(new Child4(id))
  }
  "active" -> "active" := {
    require(id < 2)
    launch(new ComplexLaunch4(id))
  }
  "active" -> "done" := {
    assert (id < 3)
  }
}
