package modbat.containers.genran

import java.util.ArrayList

import modbat.containers.Containers
import modbat.dsl._
import modbat.mbt.MBT

import scala.collection.mutable.ListBuffer

object RandomSimpleListModel {

  var sut: ListBuffer[RandomSimpleListModel] = new ListBuffer[RandomSimpleListModel]()

  @Shutdown def shutdown() {


    val testSut: Iterable[RandomSimpleListModel] = sut.groupBy( r => (r.SUT, r.n)).map(_._2.head)

    MBT.randomSearch(Seq("modbat.containers.genran.RandomSimpleListModel"), testSut.toSeq, Seq(), Seq())
  }
}


class RandomSimpleListModel extends Model {

  val SUT = new ArrayList[Integer]()
  var n = 0

  this.assert(true);

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

  "init" -> "main" := {RandomSimpleListModel.sut += this}
  "main" -> "main" := add weight 10
  "main" -> "main" := remove
// "main" -> "main" := { assert (SUT.size == n) }
  "main" -> "main" := { SUT.clear; n = 0 }
// "main" -> "main" := outOfBounds throws("IndexOutOfBoundsException")
}
