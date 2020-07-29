package modbat.util

import java.lang.Integer.MAX_VALUE

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import modbat.dsl.Action
import modbat.trace.RecordedChoice

/* Class to replace scala.util.Random with. This class can be cloned
   keeping its exact current state. */
/* TODO: Currently, only some data types are supported; other API methods
   are not needed at the moment but would have to be implemented for full
   compatibility with the existing RNG in Scala. */

class CloneableRandom(rngTrace: Array[Int], dbgTrace: Array[String])
    extends Random {
  val storedResults = new ArrayBuffer[Int](rngTrace.size)
  val resultsAsString = new ArrayBuffer[String](dbgTrace.size)
  storedResults ++= rngTrace
  resultsAsString ++= dbgTrace
  var z: Long = 0
  var w: Long = 0
  var seed: Long = 0

  // recordedChoices is used to record choices -RUI
  var recordedChoices: ListBuffer[RecordedChoice] =
    new ListBuffer[RecordedChoice]

  /** Return random seed from last time when it was actually set */
  override def getRandomSeed = seed

  def updateSeed: Unit = {
    seed = z << 32 | w
  }

  def reSeed(seed: Long) = {
    z = (seed * 1000632769) & 0xffffffffL
    w = (seed * 2019164533) & 0xffffffffL
    updateSeed
  }

  def this(z: Long, w: Long, trace: Array[Int], debugTrace: Array[String]) = {
    this(trace, debugTrace)
    this.z = z
    this.w = w
    updateSeed
  }

  def this(z: Long, w: Long) = {
    this(z, w, Array[Int](), Array[String]())
  }

  def this(seed: Long) = {
    this(Array[Int](), Array[String]())
    if ((seed >> 32) == 0) {
      reSeed(seed)
    } else {
      this.z = seed >>> 32
      this.w = seed & 0xffffffffL
    }
    updateSeed
  }

  override def clone(): CloneableRandom = {
    new CloneableRandom(z, w, storedResults.toArray, resultsAsString.toArray)
  }

  def clear = {
    storedResults.clear
    resultsAsString.clear
    recordedChoices.clear() // clear recorded choices -rui
  }

  override def nextInt(remember: Boolean = true): Int = {
    z = (36969 * (z & 65535) + (z >>> 16) & 0xffffffffL) // unsigned right shift
    w = (18000 * (w & 65535) + (w >>> 16) & 0xffffffffL)
    val result = (((z << 16) + w) & 0x7fffffff).toInt // 31-bit result
    if (remember) {
      storedResults += result
      resultsAsString += Integer.toString(result)
    }
    result
  }

  override def nextFunc(actions: Array[() => Any]) = {
    val result = super.nextFunc(actions)
    val action = new Action(result)
    resultsAsString += SourceInfo.actionInfo(action, true)
    result
  }

  override def choose(min: Int, max: Int): Int = {
    val result = super.choose(min, max)
    resultsAsString += Integer.toString(result)
    result
  }

  override def nextFloat(remember: Boolean = true) = {
    val result = super.nextFloat(remember)
    if (remember) {
      resultsAsString(resultsAsString.size - 1) =
        java.lang.Float.toString(result)
      // replace redundant info on Int
    }
    result
  }

  override def nextDouble(remember: Boolean = true) = {
    val result = super.nextDouble(remember)
    if (remember) {
      resultsAsString(resultsAsString.size - 1) =
        java.lang.Double.toString(result)
      // replace redundant info on Int
    }
    result
  }

  override def nextBoolean() = {
    val result = super.nextBoolean()
    resultsAsString += java.lang.Boolean.toString(result)
    result
  }

  /* Try to arrange an even distribution between all possible values;
     the approach below comes at the cost of performance. In the worst
     case, the probability of exiting the loop is 1/2. */
  /* Note that this function does NOT remember the string/debug
     representation by itself, as it is used by various other functions
     that themselves take care of storing the debug information */
  override def nextIntR(limit: Int, remember: Boolean): Int = {
    val leftover = MAX_VALUE % limit
    var res = nextInt(false)
    while (res >= MAX_VALUE - leftover) {
      res = nextInt(false)
    }
    val result = res % limit
    if (remember) {
      storedResults += result
    }
    result
  }

  // recordChoice method can update the choices recorded - Rui
  override def recordChoice(anyChoice: RecordedChoice): Unit = {
    recordedChoices += anyChoice
  }
  // getRecordedChoices gets recorded choices in a list -RUi
  def getRecordedChoices() = recordedChoices.toList

  def trace() = storedResults.toArray

  def debugTrace() = resultsAsString.toArray
}
