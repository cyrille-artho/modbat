package modbat.cov

import modbat.cov.TransitionRewardTypes.RewardType
import scala.collection.mutable.ListBuffer

class TransitionAverageReward {
  val backtrackedTransReward = 0.8d
  val selfTransReward = 0.4d
  val goodTransReward = 0.6d
  val failTransReward = 0.8d

  val rewardsLst: ListBuffer[Double] = new ListBuffer[Double]
  var averageReward = 0.0d

  def updateAverageReward(rewardType: RewardType): Unit = {
    rewardType match {
      case TransitionRewardTypes.BacktrackTransReward =>
        rewardsLst += backtrackedTransReward
      case TransitionRewardTypes.SelfTransReward =>
        rewardsLst += selfTransReward
      case TransitionRewardTypes.GoodTransReward =>
        rewardsLst += goodTransReward
      case TransitionRewardTypes.FailTransReward =>
        rewardsLst += failTransReward
    }
    averageReward = rewardsLst.sum / rewardsLst.length
  }
}
