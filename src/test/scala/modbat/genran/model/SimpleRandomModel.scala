package modbat.genran.model

import modbat.dsl._
import modbat.examples.ControlCounter

@RandomSearch(Array("--testclass=modbat.examples.ControlCounter", "--stop-on-error-test=true", "--time-limit=50", "--generated-limit=500"))
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
}