package modbat.examples

import modbat.dsl._

class SimpleRandomModel extends Model {

  var counter: ControlCounter = _

  // transitions
  "reset" -> "zero" := {
    counter = new ControlCounter()
  }
  "zero" -> "one" := {
   // counter.toggleSwitch()
    counter.inc()
  }
  "one" -> "random" := randomSearch(Seq("modbat.examples.ControlCounter"), Seq(counter), Seq("modbat.examples.ControlCounter.isValid()"))

}