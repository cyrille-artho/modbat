package modbat.test

import modbat.dsl._

class ComplexLaunch6 (var id: Int) extends Model {
  def this() = this(0)
  val origId = id

  @Before def before { println(origId + "before") }
  @After def after { println(origId + "after") }

  // transitions
  "init" -> "active" := skip
  "active" -> "active" := {
    id = id + 1
    launch(new Child6(id))
  }
  "active" -> "active" := {
    require(id < 2)
    launch(new ComplexLaunch6(id))
  }
  "active" -> "done" := {
    assert (id < 3)
  }
}
