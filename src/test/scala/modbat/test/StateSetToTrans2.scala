package modbat.test

import modbat.dsl._

/** Test model to force conversion of state list to transition in error trace. */
class StateSetToTrans2 extends Model {
  def fail { assert (false) }

  "init" -> "one" := skip
  "init" -> "two" := skip
  "one" -> "two" := skip
  List("init", "one", "two") -> "end" := fail
}
