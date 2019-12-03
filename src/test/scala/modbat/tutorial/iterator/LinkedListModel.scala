package modbat.tutorial.iterator

import modbat.dsl._

class LinkedListModel extends CollectionModel {
  override val collection = new java.util.LinkedList[Integer]
}
