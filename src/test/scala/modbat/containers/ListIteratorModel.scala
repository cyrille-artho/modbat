package modbat.containers

//import experiment.util.ListIterator
import java.util.ListIterator

import modbat.dsl._

class ListIteratorModel(val ldataModel: ListModel,
		    val itl: ListIterator[Integer], val idx: Integer)  extends IteratorModel(ldataModel, itl) {
/*
class ListIteratorModel(val ldataModel: ListModel,
		    val itl: ListIterator, val idx: Integer)  extends IteratorModel(ldataModel, itl) { //for the faulty version (TU library)
*/

   pos = idx - 1
 
  def this()  = this(null, null, 0) // for visualizing the model

  @States(Array("main", "modifiable")) def hasPrevious {
    if (valid) {
      assert ((pos >= 0) == itl.hasPrevious)
    } else {
      itl.hasPrevious // crash testing
    }
  }

  @States(Array("main", "modifiable")) def nextIndex {
    if (valid) {
      assert (pos+1 == itl.nextIndex )
    } else {
      itl.nextIndex // crash testing
    }
  }

  @States(Array("main", "modifiable")) def previousIndex {
    if (valid) {
      assert (pos == itl.previousIndex)
    } else {
      itl.previousIndex // crash testing
    }
  }

  @States(Array("main", "modifiable")) @Throws(Array("ConcurrentModification"))
  def concPrevious {
    require(!valid)
    itl.previous()
  }

  def previous {
    require (valid)
    require (pos >= 0)
    val res = itl.previous
    selectedElement = pos
    lastCalledNext = false
    assert (dataModel.data(pos) == res)
    pos -= 1
  }

  @States(Array("main", "modifiable")) @Throws(Array("NoSuchElementException"))
  def failingPrevious { // throws NoSuchElementException
    require (valid)
    require (pos < 0)
    itl.previous
  }

  def set {
    require(valid)
    assert(selectedElement != -1)
    val element = choose(0, 10)
    itl.set(element)
    dataModel.data(selectedElement) = element.asInstanceOf[Integer]
    dataModel.check
  } 

  def failingSet {
    require(valid)
    assert(selectedElement == -1)
    itl.set(1)
  }

  def add {
    require(valid)
    require(dataModel.n < Containers.limit)
    val element = new Integer(choose(0, 10))
    itl.add(element)
    if (pos != dataModel.n - 1) {
      for (i <- dataModel.n - 1 to pos + 1 by -1) {
        dataModel.data(i+1) = dataModel.data(i)
      }
    }
    dataModel.data(pos + 1) = element
    dataModel.n += 1
    pos += 1
    dataModel.check //Check if the array is correct after the insert
    dataModel.invalidateIt
    version = dataModel.version
    selectedElement = -1
  }

  @States(Array("main", "modifiable")) @Throws(Array("ConcurrentModification"))
  def failingAdd {
    require(!valid)
    itl.add(1)
  }

  "main" -> "modifiable" := previous
  "modifiable" -> "modifiable" := previous
  "modifiable" -> "modifiable" := set
  "main" -> "main" := add
  "modifiable" -> "main" := add 
  "main" -> "main" := failingSet throws "IllegalStateException"
}
