package modbat.containers

import java.util.{ArrayList, LinkedList, List}

import modbat.dsl._

abstract class ListModel extends Model {
  //val testData:experiment.util.List // for the faulty version (TU library)
  val testData: List[Integer] //Tested data
  
  var version = 0

  var n = 0 //Number of element in the list
  val data = new Array[Object](Containers.limit) //Copy of testData to check conformity

  def check {
    for (i <- 0 to n-1) { //Check non-null elements
      assert (data(i) == testData.get(i),
	      "At pos. " + i + ": Expected " + data(i) +
	      ", got " + testData.get(i))
    }
    for (i <- n to Containers.limit-1) { //Check null elements
      assert (data(i) == null)
    }
  }

  def invalidateIt { // invalidate all active iterators
    version += 1
  }

  def perhapsInvalid {
    // uncomment to work around Oracle inconsistency
    // for cases with remove(-1) in ArrayList
/*
    if (testData.isInstanceOf[java.util.ArrayList[Integer]]) {
      version += 1
    }
*/
  }

  def add {
    require(n < Containers.limit) 
    val element = new Integer(choose(0, 10))
    val ret = testData.add(element)   
    data(n) = element
    assert (ret) 
    n += 1
    check //Check if the array is correct after the insert
    invalidateIt
  }

  def clear {
    testData.clear
    for (i <- 0 to n - 1) {
      data(i) = null
    }
    n = 0
    check
    invalidateIt
  }

  def contains {
    val element = choose(0, 10)
    val ret = testData.contains(element)
    var ret2 = false
    if (n > 0) {
      for (i <- 0 to n-1) {
       if (data(i)==element)
         ret2 = true
      }
    }
    assert(ret == ret2)
  }

  def get {
    require(n > 0)
    val index = choose(0, n)
    assert(testData.get(index)== data(index))
  }
  
  def iterator { 
    val it = testData.iterator()
    val modelIt = new IteratorModel(this, it)
    launch(modelIt)
  }

  def listIterator { 
    val idx = choose (0, n)
    val lit = testData.listIterator(idx)
    val modelLIt = new ListIteratorModel(this, lit, idx)
    launch(modelLIt)
  }

  def remove { // == removeIdx->return the object
    require(n > 0)
    val index = choose(0, n)//was n-1 
    removeAt(index)
  }

  def removeAt(index: Int) {
    var ret: Object = null
    if (testData.isInstanceOf[LinkedList[Integer]]) {
      ret = testData.asInstanceOf[LinkedList[Integer]].remove(index)
    } else {
      assert(testData.isInstanceOf[ArrayList[Integer]])
      ret = testData.asInstanceOf[ArrayList[Integer]].remove(index)
    }
    assert(ret == data(index), "Expected " + data(index) + " at index " + index + ", got " + ret)
    n -= 1
    for (i <- index to n-1) {
      data(i) = data(i+1)
    } 
    data(n) = null
    check
    invalidateIt
  } 

  def removeObj {
    var found = false
    val obj: AnyRef = (choose (0, 10)).asInstanceOf[AnyRef]
    if (n > 0) {      
      for (i <- 0 to n-1) {
        if (data(i)==obj&&found==false) {
          n -= 1
          for (index <- i to n-1) {
            data(index) = data(index+1)
          } 
          data(n) = null
          found = true
          invalidateIt
        }
      }
    }
    val foundlib = testData.remove(obj.asInstanceOf[Object]) 
    assert(found == foundlib)
    check
  }

  def set {
      require(n > 0)
      val index = choose(0, n)
      val element = choose(0, 10)
      val ret = testData.set(index, element)
      assert(ret == data(index))
      data(index) = element.asInstanceOf[AnyRef]
      check
      //invalidateIt
  }

  def size {
    assert (testData.size == n)
  }

  def outOfBounds {
    choose(
      { () => testData.get(-1) },
      { () => testData.get(n) },
      { () => perhapsInvalid; testData.remove(-1) },
      { () => testData.remove(n) },
      { () => testData.set(-1,0) },
      { () => testData.set(n,0) },
      { () => testData.listIterator(-1) }
    )
  }
  "main" -> "main" := add weight 10
  "main" -> "main" := size
  "main" -> "main" := remove
  "main" -> "main" := get
  "main" -> "main" := clear
  "main" -> "main" := contains
  "main" -> "main" := set
  "main" -> "main" := outOfBounds throws("IndexOutOfBoundsException")
  "main" -> "main" := iterator
  "main" -> "main" := listIterator
  "main" -> "main" := removeObj
}

