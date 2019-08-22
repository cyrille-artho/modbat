package modbat.test

import modbat.dsl._

class Child2(var id: Int) extends Model {
  @Before def before { System.out.println("child:before") }
  @After def after { System.out.println("child:after") }
  // transitions
  "init" -> "running" := skip
  "running" -> "running" := {
    id = id + 1
  }
  "running" -> "done" := {
    assert (id < 5)
  }
}
