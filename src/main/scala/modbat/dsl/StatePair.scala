package modbat.dsl

import sourcecode._

class StatePair(val origin: State, val dest: State) {

  def := (action: => Any)
    (implicit line: sourcecode.Line, file: sourcecode.File): Transition = {
    Console.err.println(s"SP1+++ ${file.value}:${line.value} +++")
    new Transition(origin, dest, false, new Action(() => action))
  }

  def := (action: Action)
    (implicit line: sourcecode.Line, file: sourcecode.File): Transition = {
    Console.err.println(s"SP2+++ ${file.value}:${line.value} +++")
    new Transition(origin, dest, false, action)
  }
}
