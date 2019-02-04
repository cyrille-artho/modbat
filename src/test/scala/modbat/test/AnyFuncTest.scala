package modbat.test

import modbat.dsl._

/** Test choice generator for functions. */ 
class AnyFuncTest extends Model {
  var n = 0
  def setTo1 = { n = 1 }
  def setTo2 = { n = 2 }
  def setTo3 = { n = 3 }
  // transitions
  "ok" -> "ok" := {
    choose(
      { () => setTo1 },
      { () => setTo2 },
      { () => setTo3 }
    )
  }
  "ok" -> "end" := {
    assert (n != 2)
  }
}
