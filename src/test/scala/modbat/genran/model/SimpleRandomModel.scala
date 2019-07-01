package modbat.genran.model

import modbat.dsl._
import modbat.examples.ControlCounter

@RandomSearch(Array("modbat.examples.ControlCounter"))
class SimpleRandomModel extends Model {

  @Save var counter: ControlCounter = new ControlCounter()

  // transitions
  "reset" -> "zero" := {
    //counter = new ControlCounter()
  }
  "zero" -> "zero" := {
    counter.toggleSwitch
  }
  "zero" -> "one" := {
    counter.inc
  }
  "one" -> "two" := {
    counter.inc
  }
  "zero" -> "two" := {
    counter.inc2
  }
//  "two" -> "end" := {
//    assert (counter.value == 2)
//  }
}