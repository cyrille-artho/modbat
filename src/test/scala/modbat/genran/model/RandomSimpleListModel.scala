package modbat.genran.model

import java.util
import java.util.ArrayList

import modbat.containers.Containers
import modbat.dsl._
import modbat.genran.Wrapper.ArrayListWrapper
import modbat.mbt.MBT

import scala.collection.mutable.ListBuffer


object RandomSimpleListModel {

  var objects: ListBuffer[ArrayListWrapper] = new ListBuffer[ArrayListWrapper]()

  @Shutdown def shutdown() {

    val gropedObjects: Iterable[ArrayListWrapper] = objects.groupBy( r => r.SUT).map(_._2.head)

    MBT.randomSearch(Seq("modbat.genran.Wrapper.ArrayListWrapper"), gropedObjects.toSeq, Seq(), Seq()) //TODO add limit to the size of object? e.g 50?
  }
}

class RandomSimpleListModel extends Model {

  var w : ArrayListWrapper = _
  var n = 0

  def add {
    require(n < Containers.limit)
    val element = new Integer(choose(0, 10))
    val ret = w.add(element)
    assert (ret)
    n += 1
  }

  def remove {
    require(n > 0)
    w.remove(choose(0, n))
    n -= 1
  }

  def outOfBounds {
    choose(
      { () => w.remove(-1) },
      { () => w.remove(n) }
    )
  }

  "init" -> "main" := {
    w = new ArrayListWrapper()
    RandomSimpleListModel.objects += w
  }
  "main" -> "main" := add weight 10
  "main" -> "main" := remove
  "main" -> "main" := { assert (w.size == n) }
  "main" -> "main" := { w.clear(); n = 0 }
 // "main" -> "main" := outOfBounds throws("IndexOutOfBoundsException")
}
