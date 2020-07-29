package modbat.test

import modbat.dsl._

object ComplexLaunch5

class ComplexLaunch5 (var id: Int) extends Model {
  def this() = this(0)
  val origId = id

  @Before def before: Unit = { println(origId + "before") }
  @After def after: Unit = { println(origId + "after") }

  // transitions
  "init" -> "active" := skip
  "active" -> "active" := {
    id = id + 1
    launch(new Child5(id))
  }
  "active" -> "active" := {
    require(id < 2)
    launch(new ComplexLaunch5(id))
  }
  "active" -> "done" := {
    assert (id < 3)
  }
}
