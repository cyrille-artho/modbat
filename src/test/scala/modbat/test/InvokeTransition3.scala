package modbat.test

import modbat.dsl._

class InvokeTransition3 extends Model {
  "init" -> "init" := {
    invokeTransition("snd")
    invokeTransition("fst")
  } label "init"

  "init" -> "mid" := {
  } label "fst" weight 0

  "mid" -> "end" := {
  } label "snd" weight 0
}

