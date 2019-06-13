package modbat.genran.model

import modbat.dsl._
import modbat.examples.ControlCounter
import modbat.mbt.MBT

import scala.collection.mutable.ListBuffer

object SimpleRandomModel {

  var list: ListBuffer[ControlCounter] = new ListBuffer[ControlCounter]()

  @Shutdown def shutdown() {

    val testSut: Iterable[ControlCounter] = list.groupBy( r => (r.count, r.trueCount, r.flag)).map(_._2.head)

    MBT.randomSearch(Seq("modbat.examples.ControlCounter"), testSut.toSeq, Seq("modbat.examples.ControlCounter.isValid()"), Seq("modbat.examples.ControlCounter.isValid()"))
  }
}



@RandomSearch(Array("param1", "param2"))
class SimpleRandomModel extends Model {

  @Save var counter: ControlCounter = _

  // transitions
  "reset" -> "zero" := {
    counter = new ControlCounter
    SimpleRandomModel.list += counter
  }
  "zero" -> "zero" := {
    counter.toggleSwitch()
  }
  "zero" -> "one" := {
    counter.inc()
  }
  "one" -> "two" := {
    counter.inc()
  }
  "zero" -> "two" := {
    counter.inc2()
  }
}