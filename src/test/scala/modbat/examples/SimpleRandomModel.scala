package modbat.examples

import modbat.dsl._
import modbat.mbt.MBT

import scala.collection.mutable.ListBuffer

object SimpleRandomModel {

  var list: ListBuffer[ControlCounter] = new ListBuffer[ControlCounter]()

  @Shutdown def shutdown() {
    MBT.randomSearch(Seq("modbat.examples.ControlCounter"), list.distinct, Seq("modbat.examples.ControlCounter.isValid()"), Seq("modbat.examples.ControlCounter.isValid()"))
    //MBT.randomSearch(Seq("modbat.examples.ControlCounter"), list.distinct, Seq.empty[String], Seq.empty[String])
  }
}

class SimpleRandomModel extends Model {

  var counter: ControlCounter = _

  // transitions
  "reset" -> "zero" := {
    counter = new ControlCounter()
    SimpleRandomModel.list += counter;
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