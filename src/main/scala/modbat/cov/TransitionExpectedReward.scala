package modbat.cov

import modbat.mbt.Main
class TransitionExpectedReward {

  // todo: count passed times for assertion - Rui
  var countAssertPass = 0
  // todo: count failed times for assertion - Rui
  var countAssertFail = 0
  var totalCountAssert = 0

  // todo: count passed times for precondition - Rui
  var countPrecondPass = 0
  // todo: count failed times for precondition - Rui
  var countPrecondFail = 0
  var totalCountPrecond = 0

  var expectedReward = 0d

  def updatePrecondPassededCounter {
    countPrecondPass += 1
    calculateExpectedReward
  }

  def updatePrecondFailedCounter {
    countPrecondFail += 1
    calculateExpectedReward
  }
  def updateAssertPassedCounter {
    countAssertPass += 1
    calculateExpectedReward
  }

  def updateAssertFailedCounter {
    countAssertFail += 1
    calculateExpectedReward
  }

  def calculatePrecondExpectedReward: Double = {
    totalCountPrecond = countPrecondPass + countPrecondFail

    val precondExpectedReward: Double =
      if (totalCountPrecond != 0)
        (countPrecondPass.toDouble / totalCountPrecond.toDouble) * Main.config.precondPassReward +
          (countPrecondFail.toDouble / totalCountPrecond.toDouble) * Main.config.precondFailReward
      else 0d
    precondExpectedReward
  }

  def calculateAssertExpectedReward: Double = {
    totalCountAssert = countAssertPass + countAssertFail
    val assertExpectedReward: Double =
      if (totalCountAssert != 0)
        (countAssertPass.toDouble / totalCountAssert.toDouble) * Main.config.assertPassReward +
          (countAssertFail.toDouble / totalCountAssert.toDouble) * Main.config.assertFailReward
      else 0d
    assertExpectedReward
  }

  def calculateExpectedReward: Unit = {
    expectedReward = calculatePrecondExpectedReward + calculateAssertExpectedReward
  }
}
