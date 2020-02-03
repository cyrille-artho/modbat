package modbat.tutorial.listit

import java.util.ConcurrentModificationException
import java.util.ListIterator
import modbat.dsl._

class ListIteratorModel(val dataModel: CollectionModel,
                        val it: ListIterator[Integer]) extends Model {
  var pos = 0  

  // needs to be mutable as we also model modifications through the iterator
  var version = dataModel.version
  var lastCalledNext = false

  def size = dataModel.n

  def valid = (version == dataModel.version)

  def hasNext {
    if (valid) {
      assert ((pos < size) == it.hasNext)
    } else {
      it.hasNext
    }
  }

  def next { 
    require (valid)
    require (pos < size)
    it.next
    pos += 1
    lastCalledNext = true
  }

  def previous { // TODO: Fill in "previous"
    require (valid)
    // TODO: add correct precondition
    it.previous // call function on system under test
    // TODO: update model variable(s)
    lastCalledNext = false
  }

  // update version count in iterator and collection
  def markAsModified {
    version += 1
    dataModel.version += 1
  }

  def add {
    require (valid)
    val element = new Integer(choose(0, dataModel.N))
    it.add(element)
    pos += 1
    dataModel.n += 1
    markAsModified
  }

  def remove {
    require (valid)
    it.remove
    dataModel.n -= 1
    if (lastCalledNext)
      pos -= 1
    lastCalledNext = false
    markAsModified
  }

  def set {
    require (valid)
    val element = new Integer(choose(0, dataModel.N))
    it.set(element)
  }

  // this operation is possible in both states and does not change the state
  @States(Array("main", "modifiable")) // specify all states
  @Throws(Array("NoSuchElementException")) // specify the exception(s)
  def failingNext { // throws NoSuchElementException
    require (valid)
    require (pos >= size)
    it.next
  }

  // this operation is possible in both states and does not change the state
  @States(Array("main", "modifiable")) // specify all states
  @Throws(Array("ConcurrentModificationException")) // specify the exception(s)
  def concNext { // throws ConcurrentModificationException
    require(!valid)
    choose(
      { () => it.next() } // TODO: Add a variant testing "it.previous()"
      // note: to pass multiple options to "choose", use a comma to separate
      // all options
    )
  }

  @States(Array("main", "modifiable")) // specify all states
  def checkIdx { // TODO: specify an assertion that checks if "pos"
    // from the model is equivalent to "nextIndex" from the iterator
  }

  "main" -> "main" := hasNext
  // successfully moving the iterator allows modifying the current element
  "main" -> "modifiable" := next
  "main" -> "modifiable" := previous

  // moving the iterator in state "modifiable" keeps it there
  "modifiable" -> "modifiable" := next
  "modifiable" -> "modifiable" := previous

  // adding a new element at the iterator prevents modifications at that point
  "main" -> "main" := add
  "modifiable" -> "main" := add

   // we can only modify the current element when at a valid position
  "main" -> "main" := set throws "IllegalStateException"
  // we can modify an element at a valid position several times
  "modifiable" -> "modifiable" := set

  // we can only remove the current element when at a valid position
  "main" -> "main" := remove throws "IllegalStateException"
  // removing an element prevents modifications at that point
  "modifiable" -> "main" := remove
}
