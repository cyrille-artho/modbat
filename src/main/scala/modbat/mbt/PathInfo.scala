package modbat.mbt

import modbat.dsl.Transition

class PathInfo(val modelName:String, val modelID:Int, val transition:Transition ) {

  override def toString: String =
    s"model Name: $modelName, model ID: $modelID, transition: $transition"

}
