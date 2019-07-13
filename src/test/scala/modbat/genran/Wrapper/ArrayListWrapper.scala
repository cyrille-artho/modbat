package modbat.genran.Wrapper

import java.util

class ArrayListWrapper {

  val SUT = new util.ArrayList[Integer]()

  def add(i: Integer): Boolean = SUT.add(i)

  def remove(i: Integer): Boolean = SUT.remove(i)

  def size(): Int = SUT.size()

  def clear(): Unit = SUT.clear()
}
