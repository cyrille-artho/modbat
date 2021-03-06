package modbat.dsl

import java.io.File

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import modbat.cov.{TransitionAverageReward, TransitionCoverage}
import modbat.trace.RecordedChoice
import modbat.util.SourceInfo

/* Create a new transition. This usually happens as a side-effect
 * inside the constructor of a model; such transitions are remembered
 * and processed later. At the end of model initialization, transitions
 * from annotated methods are added; those are not kept in the
 * buffer as not to interfere with the next model instance. */
class Transition (val model:            Model,
                  var origin:           State,
                  var dest:             State,
                  val isSynthetic:      Boolean,
                  val action:           Action,
                  fullName:             String,
                  sourceLine:           Int,
                  remember:             Boolean = true) {

  def sourceInfo =
    model.mbt.sourceInfo.sourceInfoFromFullName(fullName, sourceLine)

  // NextStateNextIf records the result of the nextIf with the next state -Rui
  case class NextStateNextIf(val nextState: State, val nextIf: Boolean)

  val nonDetExcConv = ListBuffer[NextStateOnException]()
  val nextStatePredConv = ListBuffer[NextStatePredicate]()
  var coverage: TransitionCoverage = _
  // averageReward of the transition - Rui
  var averageReward: TransitionAverageReward = _

  var idx: Int = 0 // add a transition ID, initialized as 0 -RUI
  var n: Int = 0

  var recordedChoices: List[RecordedChoice] = _ // record choices -Rui

  def expectedExceptions = action.expectedExc.toList
  def nonDetExceptions = nonDetExcConv.toList
  def nextStatePredicates = nextStatePredConv.toList

  if (!isSynthetic) {
    if (remember) {
      model.pendingTransitions += this
    }
    action.nonDetExcs.foreach {
      case NonDetExc(excName, target, fullName, line) =>
        val t = new Transition(model, origin, target, isSynthetic = true, action, fullName, line)
        nonDetExcConv += new NextStateOnException(excName, t)
    }

    var i: Int = 1 // count index of next state predicate, if there are
    // several "nextIf" defintions for one transition (very rare)
    val len = action.nextStatePreds.length
    action.nextStatePreds.foreach {
      case NextStatePred(pred, state, maybe, fullName, line) =>
        val t = new Transition(model, origin, state, isSynthetic = true, new Action(model, action.transfunc), fullName, line)
        if (len > 1) t.n = i
        i += 1
        nextStatePredConv += new NextStatePredicate(pred, t, maybe)
    }
  }

  def prTrans = {
    if (isSynthetic) {
      origin.toString() + " --> " + dest
    } else {
      origin.toString() + " => " + dest
    }
  }

  def ppTrans(autoLabels: Boolean, showSkip: Boolean = false): String = {
    if (autoLabels && action.label.isEmpty) {
      assert(action.transfunc != null)
      val actionInfo = model.mbt.sourceInfo.actionInfo(action, false)
      if (actionInfo.equals(model.mbt.sourceInfo.SKIP)) {
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

  // get the next state with the result of the nextIf -Rui
  def getNextStateNextIf(nextState: State, nextIf: Boolean): NextStateNextIf =
    NextStateNextIf(nextState, nextIf)
}
