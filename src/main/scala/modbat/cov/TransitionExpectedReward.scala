package modbat.cov

import modbat.mbt.Configuration

class TransitionExpectedReward (val config: Configuration) {

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

  def updatePrecondPassededCounter: Unit = {
    countPrecondPass += 1
    calculateExpectedReward
  }

  def updatePrecondFailedCounter: Unit = {
    countPrecondFail += 1
    calculateExpectedReward
  }
  def updateAssertPassedCounter: Unit = {
    countAssertPass += 1
    calculateExpectedReward
  }

  def updateAssertFailedCounter: Unit = {
    countAssertFail += 1
    calculateExpectedReward
  }

  def calculatePrecondExpectedReward: Double = {
    totalCountPrecond = countPrecondPass + countPrecondFail

    val precondExpectedReward: Double =
      if (totalCountPrecond != 0)
        (countPrecondPass.toDouble / totalCountPrecond.toDouble) * config.precondPassReward +
          (countPrecondFail.toDouble / totalCountPrecond.toDouble) * config.precondFailReward
      else 0d
    precondExpectedReward
  }

  def calculateAssertExpectedReward: Double = {
    totalCountAssert = countAssertPass + countAssertFail
    val assertExpectedReward: Double =
      if (totalCountAssert != 0)
        (countAssertPass.toDouble / totalCountAssert.toDouble) * config.assertPassReward +
          (countAssertFail.toDouble / totalCountAssert.toDouble) * config.assertFailReward
      else 0d
    assertExpectedReward
  }

  def calculateExpectedReward: Unit = {
    expectedReward = calculatePrecondExpectedReward + calculateAssertExpectedReward
  }
}
