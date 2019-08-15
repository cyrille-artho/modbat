package modbat.dsl

import sourcecode._

class StatePair(val origin: State, val dest: State) {
  def := (action: => Any)
    (implicit line: sourcecode.Line, file: sourcecode.File): Transition = {
    new Transition(origin, dest, false, new Action(() => action), file.value, line.value)
  }

  def := (action: Action)
    (implicit line: sourcecode.Line, file: sourcecode.File): Transition = {
    new Transition(origin, dest, false, action, file.value, line.value)
  }
}
