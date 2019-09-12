package modbat.cov

class AssertionCount {
  // todo: count passed times - Rui
  var countAssertPassed = 0
  // todo: count failed times - Rui
  var countAssertFailed = 0

  def updateAssertPassedCounter {
    countAssertPassed += 1
  }

  def updateAssertFailedCounter {
    countAssertFailed += 1
  }
}
