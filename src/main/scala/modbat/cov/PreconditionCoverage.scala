package modbat.cov

import java.util.BitSet

class PreconditionCoverage {
  var count = 0
  val precondPassed = new BitSet()
  val precondFailed = new BitSet()
}
