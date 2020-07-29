package modbat.tutorial.listit

import modbat.dsl._

class LinkedListModel extends CollectionModel {
  override val collection = new java.util.LinkedList[Integer]

  override def iterator: Unit = {
    val it = collection.listIterator()
    val modelIt = new ListIteratorModel(this, it)
    launch(modelIt)
  }
}
