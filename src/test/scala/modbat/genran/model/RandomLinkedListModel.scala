package modbat.genran.model

import modbat.containers.{Containers, ListModel}

class RandomLinkedListModel extends ListModel {
  override val testData = new java.util.LinkedList[Integer]() 
  // override val testData = new experiment.util.LinkedList[Integer]() //for the faulty version (TU library)

  def chooseIdx(n: Int) = {
    if (n == 0) {
      n
    } else {
      choose (0, n)
    }
  }

  def addFirst {
    require(n < Containers.limit)
    val element = new Integer(choose(0,10))
    testData.addFirst(element)
    packAndAdd(0, element)
    invalidateIt
  }

  def addLast {
    require(n < Containers.limit)
    val element = new Integer(choose(0,10))
    testData.addLast(element)
    data(n) = element
    n += 1
    check
    invalidateIt
  }

  def addPos { // <- add(idx, elem)
    require(n < Containers.limit)
    val element = new Integer(choose(0, 10))
    val index = chooseIdx(n)
    testData.add(index, element)
    packAndAdd(index, element)
    invalidateIt
  }

  def packAndAdd(index: Int, element: AnyRef) {
    for (i <- n - 1 to index by -1) {
      data(i+1) = data(i)
    }
    data(index) = element
    n += 1
    check
  }


  def getFirst {
    require(n>0)
    val ret = testData.getFirst
    assert(ret == data(0))
  }

  def getLast {
    require(n>0)
    val ret = testData.getLast
    assert(ret == data(n-1))
  }

  def removeFirst {
    require(n>0)
    val ret = testData.removeFirst
    assert(ret == data(0))
    n -= 1
    for (i <- 0 to n-1) {
      data(i) = data(i+1)
    }
    data(n) = null
    check
    invalidateIt
  }

  def removeLast {
    require(n > 0)
    val ret = testData.removeLast
    assert(ret == data(n-1))
    data(n - 1) = null
    n -= 1
    check
    invalidateIt
  }

  override def outOfBounds {
    choose(
      { () => testData.get(-1) },
      { () => testData.get(n) },
      { () => testData.remove(-1) },
      { () => testData.remove(n) },
      { () => testData.set(-1,0) },
      { () => testData.set(n,0) },
      { () => testData.add(-1, new Integer(1)) },
      { () => testData.add(n + 1, new Integer(1)) }
    )
  }

  def noSuchElementException {
    require(n == 0)
    choose(
      { () => testData.getFirst },
      { () => testData.getLast },
      { () => testData.removeFirst },
      { () => testData.removeLast }
    )
  }

  "main" -> "main" := addPos
  "main" -> "main" := addFirst
  "main" -> "main" := addLast
  "main" -> "main" := getFirst
  "main" -> "main" := getLast
  "main" -> "main" := removeFirst
  "main" -> "main" := removeLast
  "main" -> "main" := noSuchElementException throws "NoSuchElementException"
}
