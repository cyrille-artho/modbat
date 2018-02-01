package modbat.cov

import modbat.log.Log
import modbat.dsl.State

object StateCoverage {
  def cover(s: State) {
    assert (s.coverage != null,
	    { "No coverage object for state " + s.toString })
    s.coverage.cover
  }
}

class StateCoverage {
  var count = 0

  def cover {
    count += 1
  }

  def isCovered = (count != 0)
}
