package modbat.test

import modbat.dsl._

/** Test model to force conversion of state list to transition in error trace. */
class StateSetToTrans extends Model {
  "init" -> "one" := skip
  "init" -> "two" := skip
  "one" -> "two" := skip
  List("init", "one", "two") -> "end" := { assert(false) }
}
