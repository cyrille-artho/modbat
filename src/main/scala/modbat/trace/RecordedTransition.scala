package modbat.trace

import java.lang.reflect.Field

import modbat.dsl.Transition
import modbat.mbt.MBT

class RecordedTransition(val model: MBT,
                         val transition: Transition,
                         val recordedAction: StackTraceElement = null,
                         /* state override result (if any) that was recorded */
                         val nextState: Transition = null,
                         /* exception type (if any) that was recorded */
                         val exceptionType: String = null) {
  var randomTrace: Array[Int] = null
  var debugTrace: Array[String] = null
  var updates: List[(Field, Any)] = Nil

  var recordedChoices: List[RecordedChoice] = _ // TODO: record choices -Rui

  /* nextState should override default successor state */
  def dest = {
    if (nextState == null) {
      transition.dest
    } else {
      nextState.dest
    }
  }

  /* transition that was effectively taken */
  def trans = {
    if (nextState == null) {
      transition
    } else {
      nextState
    }
  }

  override def toString = trans.toString
  // TODO: Support exceptionType
}

/** RecordedChoice */ // TODO: record choices -RUI
trait RecordedChoice {
  val recordedChoice: Any
}
//case class RecordedChoice(var recordedChoice: Any)
case class FuncChoice(recordedChoice: () => Any) extends RecordedChoice
case class BoolChoice(recordedChoice: Boolean) extends RecordedChoice
case class NumChoice(recordedChoice: Int) extends RecordedChoice
case class MaybeChoice(recordedChoice: Boolean) extends RecordedChoice
