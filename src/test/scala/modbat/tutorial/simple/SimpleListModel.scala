package modbat.tutorial.simple

import java.util.LinkedList
import modbat.dsl._

class SimpleListModel extends Model {
  val N = 10 // range of integers to choose from
  val collection = new LinkedList[Integer] // the "system under test"
  var n = 0 // Number of elements in the collection

  def add {
    val element = Integer.valueOf(choose(0, N))
    val ret = collection.add(element)   
    n += 1
    assert(ret)
  }

  def clear {
    collection.clear
    n = 0
  }

  def remove {
    val obj = Integer.valueOf(choose(0, N))
    val res = collection.remove(obj)
    n = n - 1
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
}

