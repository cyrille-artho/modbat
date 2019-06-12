package modbat.containers

import java.util.ArrayList

import modbat.dsl._

class SimpleListModel extends Model {
  val SUT = new ArrayList[Integer]()
  var n = 0

  def add {
    require(n < Containers.limit)
    val element = new Integer(choose(0, 10))
    val ret = SUT.add(element)
    assert (ret) 
    n += 1
  }

  def remove {
    require(n > 0)
    SUT.remove(choose(0, n))
    n -= 1
  } 

  def outOfBounds {
    choose(
      { () => SUT.remove(-1) },
      { () => SUT.remove(n) }
    )
  }
  "main" -> "main" := add weight 10
  "main" -> "main" := remove
  "main" -> "main" := { assert (SUT.size == n) }
  "main" -> "main" := { SUT.clear; n = 0 }
  "main" -> "main" := outOfBounds throws("IndexOutOfBoundsException")
}
