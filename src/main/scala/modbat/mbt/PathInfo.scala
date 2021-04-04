package modbat.mbt

import modbat.dsl.Transition
import modbat.mbt.TransitionQuality.Quality

/** PathInfo stores path information of current executed transition and model.
  *
  * @constructor Create a new pathInfo with a modelName, modelID,
  *              transition, and transitionQuality.
  * @param modelName The current model's name
  * @param modelID The current model's ID
  * @param transition The current executed transition
  * @param nextStateNextIf The next state and the boolean result of nextif
  * @param transitionQuality The quality of the transition, which could be OK, Backtrack, or Fail (action outcomes)
  */
class PathInfo(val modelName: String,
               val modelID: Int,
               val transition: Transition,
               val NextStateNextIf: Transition#NextStateNextIf,
               val transitionQuality: Quality = TransitionQuality.OK) {

  override def toString: String =
    s"model Name: $modelName, model ID: $modelID, transition: $transition, transition quality: $transitionQuality"
}

/** Enumeration of the transition's quality */
object TransitionQuality extends Enumeration {
  type Quality = Value
  val OK, backtrack, fail = Value
}
