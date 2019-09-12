package modbat.cov

import java.util.BitSet

class PreconditionCoverage {
  var count = 0
  val precondPassed = new BitSet()
  val precondFailed = new BitSet()

  // todo: count passed times - Rui
  var countPrecondPassed = 0
  // todo: count failed times - Rui
  var countPrecondFailed = 0

  def updatePrecondPassededCounter {
    countPrecondPassed += 1
  }

  def updatePrecondFailedCounter {
    countPrecondFailed += 1
  }
}
