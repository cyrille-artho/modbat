package modbat.dsl

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import modbat.cov.TransitionCoverage
import modbat.mbt.{MBT, Main}
import modbat.trace.RecordedChoice
import modbat.util.SourceInfo

object Transition {
  val pendingTransitions = ListBuffer[Transition]()
  def getTransitions = pendingTransitions.toList

  def clear {
    pendingTransitions.clear
  }

}

/* Create a new transition. This usually happens as a side-effect
 * inside the constructor of a model; such transitions are remembered
 * and processed later. At the end of model initialization, transitions
 * from annotated methods are added; those are not kept in the
 * buffer as not to interfere with the next model instance. */
class Transition(var origin: State,
                 var dest: State,
                 val isSynthetic: Boolean,
                 val action: Action,
                 val sourceFile: String,
                 val sourceLine: Int,
                 remember: Boolean = true) {

  // NextStateNextIf records the result of the nextIf with the next state -Rui
  case class NextStateNextIf(val nextState: State, val nextIf: Boolean)

  val nonDetExcConv = ListBuffer[NextStateOnException]()
  val nextStatePredConv = ListBuffer[NextStatePredicate]()
  var coverage: TransitionCoverage = _

  var idx: Int = 0 // add a transition ID, initialized as 0 -RUI
  var n: Int = 0

  var recordedChoices: List[RecordedChoice] = _ // record choices -Rui

  def expectedExceptions = action.expectedExc.toList
  def nonDetExceptions = nonDetExcConv.toList
  def nextStatePredicates = nextStatePredConv.toList

  if (!isSynthetic) {
    if (remember) {
      Transition.pendingTransitions += this
    }
    for (nonDetE <- action.nonDetExc) {
      val t = new Transition(origin, nonDetE._2, true, action, "????", -3)
      nonDetExcConv += new NextStateOnException(nonDetE._1, t)
    }

    var i: Int = 1 // count index of next state predicate, if there are
    // several "nextIf" defintions for one transition (very rare)
    val len = action.nextStatePred.length
    for (nextSt <- action.nextStatePred) {
      val t =
        new Transition(origin, nextSt._2, true, new Action(action.transfunc), "????", -3)
      if (len > 1) {
        t.n = i
      }
      i = i + 1
      nextStatePredConv += new NextStatePredicate(nextSt._1, t, nextSt._3)
    }
  }

  def prTrans = {
    if (isSynthetic) {
      origin + " --> " + dest
    } else {
      origin + " => " + dest
    }
  }

  def ppTrans(showSkip: Boolean = false): String = {
    if (Main.config.autoLabels && action.label.isEmpty) {
      assert(action.transfunc != null)
      val actionInfo = SourceInfo.actionInfo(action, false)
      if (actionInfo.equals(SourceInfo.SKIP)) {
        if (showSkip) {
          return "[skip]"
        } else {
          return ""
        }
      }
      if (!actionInfo.isEmpty) {
        return actionInfo
      }
    }
    toString
  }

  override def toString() = {
    if (action.label.isEmpty) {
      if (n == 0) {
        prTrans
      } else {
        prTrans + " (" + n + ")"
      }
    } else {
      action.label
    }
  }

  def launchesAndChoices: List[SourceInfo.InternalAction] = {
    SourceInfo.launchAndChoiceInfo(action)
  }
  // get the next state with the result of the nextIf -Rui
  def getNextStateNextIf(nextState: State, nextIf: Boolean): NextStateNextIf =
    NextStateNextIf(nextState, nextIf)
}
