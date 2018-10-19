package modbat.mbt

import modbat.dsl.Transition
import modbat.mbt.TransitionQuality.Quality

class PathInfo(val modelName:String, val modelID:Int, val transition:Transition, val transitionQuality:Quality = TransitionQuality.OK) {

  override def toString: String =
    s"model Name: $modelName, model ID: $modelID, transition: $transition, transition quality: $transitionQuality"

}

object TransitionQuality extends Enumeration {
  type Quality = Value
  val OK, backtrack, fail = Value
}