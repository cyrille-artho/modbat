package modbat.dsl

import scala.collection.mutable.ListBuffer

class StateSet(preStates: List[String], postState: String) {
  val dest = new State(postState)

  def := (action: => Any)
    (implicit line: sourcecode.Line, fullName: sourcecode.FullName):
    List[Transition] = {
    val trans = new ListBuffer[Transition]
    val a = new Action(() => action)
    for (origin <- preStates) {
      trans +=
	new Transition(new State(origin), dest, false, a, fullName.value, line.value)
    }
    trans.toList
  }
}
