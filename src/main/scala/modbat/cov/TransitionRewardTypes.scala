package modbat.cov

object TransitionRewardTypes extends Enumeration {
  type RewardType = Value
  val SelfTransReward, BacktrackTransReward, GoodTransReward, FailTransReward =
    Value
}
