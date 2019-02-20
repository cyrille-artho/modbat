package modbat.test

import modbat.dsl._

class InvokeTransition1 extends Model {
  var first = true

  "init" -> "init" := {
    assert(first)
    invokeTransition("invoked")
    first = false
  } label "init"

  "init" -> "end" := {
    assert(!first)
  } label "invoked" weight 0
}

