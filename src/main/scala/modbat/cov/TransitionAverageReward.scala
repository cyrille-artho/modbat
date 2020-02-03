package modbat.cov

import modbat.cov.TransitionRewardTypes.RewardType
import scala.collection.mutable.ListBuffer
import modbat.mbt.Main

class TransitionAverageReward {

  val rewardsLst: ListBuffer[Double] = new ListBuffer[Double]
  var averageReward = 0.0d

  def updateAverageReward(rewardType: RewardType): Unit = {

    rewardType match {
      case TransitionRewardTypes.BacktrackTransReward =>
        rewardsLst += Main.config.backtrackTReward
      case TransitionRewardTypes.SelfTransReward =>
        rewardsLst += Main.config.selfTReward
      case TransitionRewardTypes.GoodTransReward =>
        rewardsLst += Main.config.goodTReward
      case TransitionRewardTypes.FailTransReward =>
        rewardsLst += Main.config.failTReward
    }
    averageReward = rewardsLst.sum / rewardsLst.length
  }
}
