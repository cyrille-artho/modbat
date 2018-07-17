package modbat.dsl

abstract class NextStateOverride {
  /* Target state is represented by transition function containing
   * predicate action and arc to target state */
  def target: Transition
}
