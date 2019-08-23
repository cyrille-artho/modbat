package modbat.dsl

class StatePair(val model: Model, val origin: State, val dest: State) {

  def := (action: => Any): Transition = {
    new Transition(model, origin, dest, false, new Action(model, () => action))
  }

  def := (action: Action): Transition = {
    new Transition(model, origin, dest, false, action)
  }
}
