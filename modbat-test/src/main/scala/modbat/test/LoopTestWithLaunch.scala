package modbat.test

import modbat.dsl._

class LoopTestWithLaunch (var i: Int) extends Model {

  def this() = this(0)

  // transitions
  "ok" -> "ok" := {
    i = i + 1
    Console.out.println(i)
    assert (i < 3)
  }
  "ok" -> "ok" := {
    launch(new LoopTestWithLaunch(i))
    // should "overflow" for child instances
    // because separate loop limit should apply
  }
}
