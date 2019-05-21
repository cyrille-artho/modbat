package modbat.util

import java.lang.Integer.MAX_VALUE

import modbat.log.Log
import modbat.trace.RecordedChoice

trait Random {
  def getRandomSeed: Long // get original random seed
  // this will differ from the current state if next... was used after the
  // seed was set

  def nextFunc(actions: Array[() => Any]) = actions(nextInt(actions.size))

  def nextBoolean(): Boolean = {
    return ((nextInt(true) % 2) == 0)
  }

  def nextInt(remember: Boolean = true): Int

  def nextInt(limit: Int) = nextIntR(limit, true)

  def nextIntR(limit: Int, remember: Boolean): Int

  def nextFloat(remember: Boolean = true): Float = {
    return (nextInt(remember).toDouble / MAX_VALUE).toFloat
  }

  def nextDouble(remember: Boolean = true) = {
    val res = nextInt(remember)
    res.toDouble / (1L << 31).toDouble
  }

  def choose(min: Int, max: Int): Int = {
    val d = max - min
    if (d < 0) throw new IllegalArgumentException("Invalid range")
    else {
      val res = min + nextIntR(d, true)
      Log.fine("choose(" + min + ", " + max + ") = " + res)
      res
    }
  }

  // record choice - Rui
  def recordChoice(anyChoice: RecordedChoice): Unit
}
