package modbat.test

import modbat.dsl._

class TestObserver2(val target: CounterModel) extends Observer {
  // transitions
  "zero" -> "one" := {
    require(target.i == 1)
    System.out.println("one")
  }
  "one" -> "many" := {
    require(target.i > 1)
    System.out.println("many")
  }
  "many" -> "many" := {
    assert(target.i < 4)
  } weight 5.0
}
