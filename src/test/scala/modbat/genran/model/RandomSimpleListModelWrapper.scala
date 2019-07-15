package modbat.genran.model

import modbat.dsl._
import modbat.genran.Wrapper.ArrayListWrapper

/**
  * Code from https://bitbucket.org/quentin-gros/containers/src/master/
  */
@RandomSearch(Array("--testclass=modbat.genran.Wrapper.ArrayListWrapper", "--stop-on-error-test=true", "--time-limit=50", "--generated-limit=500"))
class RandomSimpleListModelWrapper extends Model {

  @Save var w : ArrayListWrapper = _
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
  }
  "main" -> "main" := add weight 10
  "main" -> "main" := remove
  "main" -> "main" := { assert (w.size == n) }
  "main" -> "main" := { w.clear(); n = 0 }
  "main" -> "main" := outOfBounds throws("IndexOutOfBoundsException")
}
