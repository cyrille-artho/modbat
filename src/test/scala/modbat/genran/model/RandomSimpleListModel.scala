package modbat.genran.model

import java.util

import modbat.containers.Containers
import modbat.dsl._

@RandomSearch(Array("java.util.ArrayList"))
class RandomSimpleListModel extends Model {

  @Save var w : util.ArrayList[Integer] = _
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
    w = new util.ArrayList[Integer]()
  }
  "main" -> "main" := add weight 10
  "main" -> "main" := remove
  "main" -> "main" := { assert (w.size == n) }
  "main" -> "main" := { w.clear(); n = 0 }
 // "main" -> "main" := outOfBounds throws("IndexOutOfBoundsException")
}
