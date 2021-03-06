package modbat.dsl

import java.lang.reflect.Method
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

class Action(val model: Model, val transfunc: () => Any, val method: Method = null) {
  val mbt = model.mbt
  val expectedExc = ListBuffer[Regex]()
  val nonDetExcs = ListBuffer[NonDetExc]()
  val nextStatePreds = ListBuffer[NextStatePred]()
  var label: String = ""
  var weight = 1.0
  var immediate = false // if true, do not switch between model
  // instances for next step; immediately execute this model again
  var stayTime: Option[(Int, Int)] = None

  def nonDetExceptions = nonDetExcs.toList

  def nextStatePredicates = nextStatePreds.toList

  def label(name: String): Action = {
    label = name
    this
  }

  def throws(excTypes: String*): Action = throws(excTypes.toArray)

  def throws(excTypes: Array[String]): Action = {
    for (excType <- excTypes) {
      expectedExc += new Regex(excType)
    }
    this
  }

  def catches(excToState: (String, String)*)
    (implicit line: sourcecode.Line, fullName: sourcecode.FullName): Action = {
    for (excMapping <- excToState) {
      val detExc = excMapping match {
        case (name, state) =>
          NonDetExc(new Regex(name), new State(state), fullName.value, line.value)
      }
      nonDetExcs += detExc
    }
    immediate = true
    this
  }

  def maybeNextIf(conditions: (() => Boolean, String)*)
    (implicit line: sourcecode.Line, fullName: sourcecode.FullName): Action = {
    conditions.foreach {
      case (pred, stateName) =>
        val nextStatePred = NextStatePred(pred, new State(stateName), maybe = true, fullName.value, line.value)
        nextStatePreds += nextStatePred
    }
    this
  }

  def nextIf(conditions: (() => Boolean, String)*)
    (implicit line: sourcecode.Line, fullName: sourcecode.FullName): Action = {
    conditions.foreach {
      case (pred, stateName) =>
        val nextStatePred = NextStatePred(pred, new State(stateName), maybe = false, fullName.value, line.value)
        nextStatePreds += nextStatePred
    }
    this
  }

  def weight(w: Double): Action = {
    weight = w
    this
  }

  def stay(time: Int): Action = {
    stayTime = Some(time, time)
    this
  }

  def stay(times: (Int, Int)): Action = {
    stayTime = Some(times)
    this
  }

  def or_else(action: => Any) = {
    if (mbt.rng.nextFloat(true) < mbt.config.maybeProbability) {
      mbt.or_else = true
      transfunc() // Call function that or_else is chained on,
      // but do not use RNG as "maybe" branch should always be taken.
    } else {
      action
    }
  }
}
