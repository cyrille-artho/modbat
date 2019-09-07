package modbat.cov

import modbat.cov.TransitionRewardTypes.RewardType
import scala.collection.mutable.ListBuffer

class TransitionReward {
  val backtrackedTransReward = 0.1d
  val selfTransReward = 0.4d
  val goodTransReward = 0.6d
  val failTransReward = 0.8d

  val rewardsLst: ListBuffer[Double] = new ListBuffer[Double]
  var averageReward = 0.0d

  def updateRewards(rewardType: RewardType): Unit = {
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

object TransitionRewardTypes extends Enumeration {
  type RewardType = Value
  val SelfTransReward, BacktrackTransReward, GoodTransReward, FailTransReward =
    Value
}
