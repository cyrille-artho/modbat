package modbat.tutorial.iterator

import java.util.ConcurrentModificationException
import java.util.Iterator
import modbat.dsl._

class IteratorModel(val dataModel: CollectionModel,
		    val it: Iterator[Integer]) extends Model {
  var pos = 0  
  val version = dataModel.version

  def valid = (version == dataModel.version)

  def actualSize = dataModel.collection.size

  def hasNext {
    if (valid) {
      assert ((pos < actualSize) == it.hasNext)
    } else {
      it.hasNext
    }
  }

  def next { 
    require (valid)
    require (pos < actualSize)
    it.next
    pos += 1
  }

  def failingNext { // throws NoSuchElementException
    require (valid)
    require (pos >= actualSize)
    it.next
  }

  def concNext { // throws ConcurrentModificationException
    require(!valid)
    it.next
  }

  "main" -> "main" := hasNext
  "main" -> "main" := next
  "main" -> "main" := failingNext throws "NoSuchElementException"
  "main" -> "main" := concNext throws "ConcurrentModificationException"
}
