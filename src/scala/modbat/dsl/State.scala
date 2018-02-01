package modbat.dsl

import modbat.cov.StateCoverage

class State (val name: String) {
  override def toString = name
  var coverage: StateCoverage = _
}
