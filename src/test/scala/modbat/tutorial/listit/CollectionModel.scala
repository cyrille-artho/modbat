package modbat.tutorial.listit

import java.util.Collection
import java.util.Iterator
import modbat.dsl._

abstract class CollectionModel extends Model {
  val N = 10 // range of integers to choose from
  val collection: Collection[Integer] // the "system under test"
  var version = 0 // how many modifications were made on the collection
  var n = 0 // Number of elements in the collection

  def invalidateIt { // invalidate all active iterators
    version += 1
  }

  def add {
    val element = new Integer(choose(0, N))
    val ret = collection.add(element)   
    n += 1
    assert(ret)
    invalidateIt
  }

  def clear {
    collection.clear
    n = 0
    invalidateIt
  }

  def remove {
    val obj = new Integer(choose(0, N))
    val res = collection.remove(obj)
    if (res) {
      n = n - 1
      invalidateIt
    }
  }

  def iterator { 
    val it = collection.iterator()
    val modelIt = new IteratorModel(this, it)
    launch(modelIt)
  }

  def size {
    assert (collection.size == n,
	    "Predicted size: " + n +
	    ", actual size: " + collection.size)
  }

  "main" -> "main" := add
  "main" -> "main" := size
  "main" -> "main" := clear
  "main" -> "main" := remove
  "main" -> "main" := iterator
}

