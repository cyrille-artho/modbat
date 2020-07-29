package modbat.test

import modbat.dsl._

object Child5

class Child5(var id: Int) extends Model {
  val origId = id
  @Before def child_before: Unit = { println("child " + origId + ":before") }
  @After def child_after: Unit = { println("child " + origId + ":after") }
  // transitions
  "init" -> "running" := skip
  "running" -> "running" := {
    id = id + 1
  }
  "running" -> "done" := {
    assert (id < 5)
  }
}
