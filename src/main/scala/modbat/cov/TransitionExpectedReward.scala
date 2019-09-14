package modbat.cov
import modbat.log.Log
class TransitionExpectedReward {
  val rewardPrecondPass = 0.7d
  val rewardPrecondFail = 0.3d
  val rewardAssertPass = 0.4d
  val rewardAssertFail = 0.6d

  // todo: count passed times for assertion - Rui
  var countAssertPassed = 0
  // todo: count failed times for assertion - Rui
  var countAssertFailed = 0
  var totalCountAssert = 0

  // todo: count passed times for precondition - Rui
  var countPrecondPassed = 0
  // todo: count failed times for precondition - Rui
  var countPrecondFailed = 0
  var totalCountPrecond = 0

  var expectedReward = 0d

  def updatePrecondPassededCounter {
    countPrecondPassed += 1
    calculateExpectedReward
  }

  def updatePrecondFailedCounter {
    countPrecondFailed += 1
    calculateExpectedReward
  }
  def updateAssertPassedCounter {
    countAssertPassed += 1
    calculateExpectedReward
  }

  def updateAssertFailedCounter {
    countAssertFailed += 1
    calculateExpectedReward
  }

  def calculatePrecondExpectedReward: Double = {
    //Log.debug("*$# countPrecondPassed:" + countPrecondPassed)
    //Log.debug("*$# countPrecondFailed:" + countPrecondFailed)
    totalCountPrecond = countPrecondPassed + countPrecondFailed
    //Log.debug("*$# totalCountPrecond:" + totalCountPrecond)

    val precondExpectedReward: Double =
      if (totalCountPrecond != 0)
        (countPrecondPassed.toDouble / totalCountPrecond.toDouble) * rewardPrecondPass + (countPrecondFailed.toDouble / totalCountPrecond.toDouble) * rewardPrecondFail
      else 0d
    //Log.debug("*$# precondition expected reward:" + precondExpectedReward)
    precondExpectedReward
  }

  def calculateAssertExpectedReward: Double = {
    //Log.debug("*$# countAssertPassed:" + countAssertPassed)
    //Log.debug("*$# countAssertFailed:" + countAssertFailed)
    totalCountAssert = countAssertPassed + countAssertFailed
    //Log.debug("*$# totalCountAssert:" + totalCountAssert)
    val assertExpectedReward: Double =
      if (totalCountAssert != 0)
        (countAssertPassed.toDouble / totalCountAssert.toDouble) * rewardAssertPass + (countAssertFailed.toDouble / totalCountAssert.toDouble) * rewardAssertFail
      else 0d
    //Log.debug("*$# assertion expected reward:" + assertExpectedReward)
    assertExpectedReward
  }

  def calculateExpectedReward: Unit = {
    expectedReward = calculatePrecondExpectedReward + calculateAssertExpectedReward
    //Log.debug("*$# expected reward:" + expectedReward)
  }
}
