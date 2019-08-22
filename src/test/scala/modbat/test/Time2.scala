package modbat.test

import modbat.dsl._

class Time2() extends Model {
  var c1: TimeChild1 = _
  var i = 0
  def sum = i + c1.i 
  "p-init" -> "p-main" := {
    System.out.println("launch c1")
    c1 = new TimeChild1(this)
    launch(c1)
  }
  "p-main" -> "p-main" := {
    System.out.println("parent add: i="+i)
    if(sum < 10) i += 1
  } stay 2
  "p-main" -> "p-end" := {
    System.out.println("parent end: i="+i)
    require(sum >= 10)
    assert(i == 6)
  }
}

class TimeChild1(parent: Time2) extends Model {
  var i = 0
  "c1-init" -> "c1-init" := {
    System.out.println("c1 add: i="+i)
    if(parent.sum < 10) i += 1
  } stay 3
  "c1-init" -> "c1-end" := {
    System.out.println("c1 end: i="+i)
    require(parent.sum >= 10)
    assert(i == 4)
  }
}
