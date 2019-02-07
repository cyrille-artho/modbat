package modbat.test

import modbat.dsl._

class InvokeTransition1 extends Model {
  "init" -> "mid" :={
    invokeTransition("invoked")
  } label "init"

  "mid" -> "end" :={
  } label "invoked" weight 0

  "mid" -> "end" :={
    //assert(false)
  } label "false"
}

