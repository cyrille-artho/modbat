package modbat.dsl

import sourcecode._

class StatePair(val model: Model, val origin: State, val dest: State) {
  def := (action: => Any)
    (implicit line: sourcecode.Line, fullName: sourcecode.FullName): Transition = {
    new Transition(model, origin, dest, false, new Action(model, () => action), fullName.value, line.value)
  }

  def := (action: Action)
    (implicit line: sourcecode.Line, fullName: sourcecode.FullName): Transition = {
    new Transition(model, origin, dest, false, action, fullName.value, line.value)
  }
}
