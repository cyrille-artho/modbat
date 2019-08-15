package modbat.dsl

import sourcecode._

class StatePair(val origin: State, val dest: State) {
  def := (action: => Any)
    (implicit line: sourcecode.Line, fullName: sourcecode.FullName): Transition = {
    new Transition(origin, dest, false, new Action(() => action), fullName.value, line.value)
  }

  def := (action: Action)
    (implicit line: sourcecode.Line, fullName: sourcecode.FullName): Transition = {
    new Transition(origin, dest, false, action, fullName.value, line.value)
  }
}
