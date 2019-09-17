package modbat.cov
import modbat.log.Log
class TransitionExpectedReward {
  val precondPassReward = 0.7d
  val precondFailReward = 0.3d
  val assertPassReward = 0.4d
  val assertFailReward = 0.6d

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
    //Log.debug("*$# countPrecondPassed:" + countPrecondPassed)
    //Log.debug("*$# countPrecondFailed:" + countPrecondFailed)
    totalCountPrecond = countPrecondPass + countPrecondFail
    //Log.debug("*$# totalCountPrecond:" + totalCountPrecond)

    val precondExpectedReward: Double =
      if (totalCountPrecond != 0)
        (countPrecondPass.toDouble / totalCountPrecond.toDouble) * precondPassReward +
          (countPrecondFail.toDouble / totalCountPrecond.toDouble) * precondFailReward
      else 0d
    //Log.debug("*$# precondition expected reward:" + precondExpectedReward)
    precondExpectedReward
  }

  def calculateAssertExpectedReward: Double = {
    //Log.debug("*$# countAssertPassed:" + countAssertPassed)
    //Log.debug("*$# countAssertFailed:" + countAssertFailed)
    totalCountAssert = countAssertPass + countAssertFail
    //Log.debug("*$# totalCountAssert:" + totalCountAssert)
    val assertExpectedReward: Double =
      if (totalCountAssert != 0)
        (countAssertPass.toDouble / totalCountAssert.toDouble) * assertPassReward +
          (countAssertFail.toDouble / totalCountAssert.toDouble) * assertFailReward
      else 0d
    //Log.debug("*$# assertion expected reward:" + assertExpectedReward)
    assertExpectedReward
  }

  def calculateExpectedReward: Unit = {
    expectedReward = calculatePrecondExpectedReward + calculateAssertExpectedReward
    //Log.debug("*$# expected reward:" + expectedReward)
  }
}
