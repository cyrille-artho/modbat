package modbat.dsl

class StatePair(val origin: State, val dest: State) {

  def := (action: => Any): Transition = {
    new Transition(origin, dest, false, new Action(() => action))
  }

  def := (action: Action): Transition = {
    new Transition(origin, dest, false, action)
  }
}
