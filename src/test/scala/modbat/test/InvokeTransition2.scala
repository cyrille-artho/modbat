package modbat.test

import modbat.dsl._

class InvokeTransition2 extends Model {
  "init" -> "init" := {
    invokeTransition("fst")
    invokeTransition("snd")
  } label "init"

  "init" -> "mid" := {
  } label "fst" weight 0

  "mid" -> "end" := {
  } label "snd" weight 0

  //"mid" -> "end" := {
  //  assert(false)
  //} label "fail"
}

