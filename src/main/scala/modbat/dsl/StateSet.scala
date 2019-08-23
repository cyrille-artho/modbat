package modbat.dsl

import scala.collection.mutable.ListBuffer

class StateSet(val model: Model, preStates: List[String], postState: String) {
  val dest = new State(postState)

  def := (action: => Any): List[Transition] = {
    val trans = new ListBuffer[Transition]
    val a = new Action(model, () => action)
    for (origin <- preStates) {
      trans +=
	new Transition(model, new State(origin), dest, false, a)
    }
    trans.toList
  }

  def := (action: Action): List[Transition] = {
    val trans = new ListBuffer[Transition]
    for (origin <- preStates) {
      trans += new Transition(model, new State(origin), dest, false, action)
    }
    trans.toList
  }
}
