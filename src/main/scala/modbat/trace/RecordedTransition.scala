package modbat.trace

import java.lang.reflect.Field

import modbat.dsl.Transition
import modbat.mbt.MBT

class RecordedTransition (val model:		MBT,
			  val transition:	Transition,
			  val recordedAction:	StackTraceElement = null,
			  /* state override result (if any) that was recorded */
			  val nextState:	Transition = null,
			  /* exception type (if any) that was recorded */
			  val exceptionType:	String = null) {
  var randomTrace: Array[Int] = null 
  var debugTrace: Array[String] = null
  var updates: List[(Field, Any)] = Nil

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
