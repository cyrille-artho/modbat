package modbat.dsl

import java.lang.reflect.Method
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import modbat.log.Log
import modbat.mbt.MBT

class Action(val transfunc: () => Any, val method: Method = null) {
  val expectedExc = ListBuffer[Regex]()
  val nonDetExc = ListBuffer[(Regex, State)]()
  val nextStatePred = ListBuffer[(() => Boolean, State, Boolean)]()
  var label: String = ""
  var weight = 1.0
  var immediate = false // if true, do not switch between model
  // instances for next step; immediately execute this model again
  var stayTime: Option[(Int, Int)] = None

  def nonDetExceptions = nonDetExc.toList

  def nextStatePredicates = nextStatePred.toList

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

  def catches(excToState: (String, String)*): Action = {
    for (excMapping <- excToState) {
      nonDetExc += ((new Regex(excMapping._1), new State(excMapping._2)))
    }
    immediate = true
    this
  }

  def maybeNextIf(conditions: (() => Boolean, String)*): Action = {
    for (cond <- conditions) {
      nextStatePred += ((cond._1, new State(cond._2), true))
    }
    this
  }

  def nextIf(conditions: (() => Boolean, String)*): Action = {
    for (cond <- conditions) {
      nextStatePred += ((cond._1, new State(cond._2), false))
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
    if (MBT.rng.nextFloat(true) < MBT.maybeProbability) {
      MBT.or_else = true
      transfunc() // Call function that or_else is chained on,
      // but do not use RNG as "maybe" branch should always be taken.
    } else {
      action
    }
  }
}
