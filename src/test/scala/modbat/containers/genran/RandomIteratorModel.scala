package modbat.containers.genran

//import experiment.util.Iterator
import java.util.Iterator

import modbat.containers.ListModel
import modbat.dsl._

class RandomIteratorModel(val dataModel: ListModel,
                          val it: Iterator[Integer]) extends Model {
  /*
  class IteratorModel(val dataModel: ListModel,
      val it: Iterator)  extends Model { // for the faulty version (TU library)
  */

  var pos = -1
  var version = dataModel.version
  var selectedElement = -1
  var lastCalledNext = false
  // work around inconsistent behavior after remove(-1) on parent data

  @States(Array("main", "modifiable")) def hasNext {
    if (valid) {
      assert((dataModel.n - 1 > pos) == it.hasNext)
    } else {
      it.hasNext // crash testing
    }
  }

  def next {
    require(valid)
    require(pos < dataModel.n - 1)
    val res = it.next
    pos += 1
    selectedElement = pos
    assert(dataModel.data(pos) == res)
    lastCalledNext = true
  }

  def failingNext { // throws NoSuchElementException
    require(valid)
    require(pos >= dataModel.n - 1)
    it.next
  }

  def remove {
    require(valid)
    assert(selectedElement != -1)
    //dataModel.removeAt(selectedElement) // or pos - 1?
    //This is not an iterator function
    it.remove()
    dataModel.n -= 1
    if (lastCalledNext)
      pos -= 1
    for (i <- selectedElement to dataModel.n - 1) {
      dataModel.data(i) = dataModel.data(i + 1)
    }
    dataModel.data(dataModel.n) = null
    dataModel.check
    // removeAt already updates the data structures
    selectedElement = -1
    lastCalledNext = false
    dataModel.invalidateIt
    version = dataModel.version
  }

  def valid = (version == dataModel.version)

  @States(Array("main", "modifiable"))
  @Throws(Array("ConcurrentModification"))
  def concNext {
    require(!valid)
    it.next
  }

  def concRemove {
    require(!valid)
    it.remove()
  }

  def failingRemove {
    require(valid)
    it.remove
  }

  "main" -> "modifiable" := next
  "modifiable" -> "modifiable" := next
  "main" -> "main" := failingNext throws "NoSuchElementException"
  "modifiable" -> "modifiable" := concRemove throws "ConcurrentModificationException"
  "main" -> "main" := concRemove throws("ConcurrentModificationException", "IllegalStateException")
  "main" -> "main" := failingRemove throws "IllegalStateException"
  "modifiable" -> "main" := remove
  //  "main" -> "end" := skip weight 0.001
  //  "main" -> "main" := hasNext catches ("AssertionError" -> "end")
  //  "modified" -> "modified" := hasNext catches ("AssertionError" -> "end")
}
