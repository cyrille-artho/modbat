package modbat.test

import modbat.dsl._

class Time2() extends Model {
  var c1: TimeChild1 = _
  var i = 0
  def sum = i + c1.i 
  "p-init" -> "p-main" := {
    c1 = new TimeChild1(this)
    launch(c1)
  }
  "p-main" -> "p-main" := {
    if(sum < 10) i += 1
  } stay 2
  "p-main" -> "p-end" := {
    require(sum >= 10)
    assert(i == 6)
  }
}

class TimeChild1(parent: Time2) extends Model {
  var i = 0
  "c1-init" -> "c1-init" := {
    if(parent.sum < 10) i += 1
  } stay 3
  "c1-init" -> "c1-end" := {
    require(parent.sum >= 10)
    assert(i == 4)
  }
}
