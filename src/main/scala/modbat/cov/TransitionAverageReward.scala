package modbat.cov

import modbat.cov.TransitionRewardTypes.RewardType
import scala.collection.mutable.ListBuffer
import modbat.mbt.Main

class TransitionAverageReward {
//  val backtrackedTransReward = 0.8d
//  val selfTransReward = 0.4d
//  val goodTransReward = 0.6d
//  val failTransReward = 0.8d

  val rewardsLst: ListBuffer[Double] = new ListBuffer[Double]
  var averageReward = 0.0d

  def updateAverageReward(rewardType: RewardType): Unit = {
    rewardType match {
      case TransitionRewardTypes.BacktrackTransReward =>
        rewardsLst += Main.config.backtrackTReward //backtrackedTransReward
      case TransitionRewardTypes.SelfTransReward =>
        rewardsLst += Main.config.selfTReward //selfTransReward
      case TransitionRewardTypes.GoodTransReward =>
        rewardsLst += Main.config.goodTReward //goodTransReward
      case TransitionRewardTypes.FailTransReward =>
        rewardsLst += Main.config.failTReward //failTransReward
    }
    averageReward = rewardsLst.sum / rewardsLst.length
  }
}
