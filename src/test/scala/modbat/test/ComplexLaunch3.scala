package modbat.test

import modbat.dsl._

object ComplexLaunch3 {
  @Init def init { System.out.println("init") }
  @Shutdown def shutdown { System.out.println("shutdown") }

  @Before def companion_before { System.out.println("companion-before") }
  @After def companion_after { System.out.println("companion-after") }
}

class ComplexLaunch3 (var id: Int) extends Model {
  def this() = this(0)
  val origId = id

  @Before def before { System.out.println(origId + "before") }
  @After def after { System.out.println(origId + "after") }

  // transitions
  "init" -> "active" := skip
  "active" -> "active" := {
    id = id + 1
    launch(new Child3(id))
  }
  "active" -> "active" := {
    require(id < 2)
    launch(new ComplexLaunch3(id))
  }
  "active" -> "done" := {
    assert (id < 3)
  }
}
