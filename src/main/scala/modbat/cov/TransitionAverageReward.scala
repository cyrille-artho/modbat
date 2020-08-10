package modbat.cov

import modbat.cov.TransitionRewardTypes.RewardType
import modbat.mbt.Configuration
import scala.collection.mutable.ListBuffer

class TransitionAverageReward(val config: Configuration) {

  val rewardsLst: ListBuffer[Double] = new ListBuffer[Double]
  var averageReward = 0.0d

  def updateAverageReward(rewardType: RewardType): Unit = {

    rewardType match {
      case TransitionRewardTypes.BacktrackTransReward =>
        rewardsLst += config.backtrackTReward
      case TransitionRewardTypes.SelfTransReward =>
        rewardsLst += config.selfTReward
      case TransitionRewardTypes.GoodTransReward =>
        rewardsLst += config.goodTReward
      case TransitionRewardTypes.FailTransReward =>
        rewardsLst += config.failTReward
    }
    averageReward = rewardsLst.sum / rewardsLst.length
  }
}
