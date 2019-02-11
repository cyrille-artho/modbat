package modbat.test

import modbat.dsl._

class InvokeTransition4 extends Model {
  var n = 0
  "init" -> "mid" := {
    invokeTransition("add")
    invokeTransition("add")
    invokeTransition("add")
    invokeTransition("end")
  } label "init"

  "mid" -> "mid" := {
    assert(n < 3)
    n += 1
  } label "add" weight 0

  "mid" -> "end" := {
    assert(n==3)
  } label "end" weight 1
}

