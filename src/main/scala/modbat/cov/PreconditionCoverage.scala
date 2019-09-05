package modbat.cov

import java.util.BitSet

class PreconditionCoverage {
  var count = 0
  val precondPassed = new BitSet()
  val precondFailed = new BitSet()
  //todo : count failed times - rui
  var countPrecondFailed = 0

  def updatePrecondFailedCounter {
    countPrecondFailed += 1
  }
}
