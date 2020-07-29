package modbat.cov

/* Class to store transition info (so it can be extended from boolean to
   a numerical value), and to manage handling of data */

import modbat.dsl.Transition
import modbat.dsl.NextStateOverride
import modbat.mbt.MBT
import modbat.trace.Ok
import modbat.trace.RecordedTransition

object TransitionCoverage {

  def cover(model: MBT,
            t: Transition,
            nextState: Transition = null,
            excType: String = null,
            sameAgain: Boolean = false) = {
    assert(t.coverage != null, {
      "No coverage object for transition " + t.toString
    })
    if (nextState == null) {
      setCoverageAndState(t, model)
    } else {
      setCoverageAndState(nextState, model)
    }
    // do not use stack trace information as call has already happened
    // so that information is no longer directly available
    (Ok(sameAgain), new RecordedTransition(model, t, null, nextState, excType))
  }

  def setCoverageAndState(t: Transition, model: MBT): Unit = {
    t.coverage.cover
    StateCoverage.cover(t.dest)
    assert(model != null)
    model.currentState = t.dest
  }

  def reuseCoverageInfo(instance: MBT, master: MBT, className: String): Unit = {
    // copy values of previous equivalent instance for performance
    // and correct coverage information
    val transIt = instance.transitions.iterator
    val mTransIt = master.transitions.iterator
    while (transIt.hasNext && mTransIt.hasNext) {
      /*      assert (mTransIt.hasNext,
	{ "Master has no next transition but current instance has " +
	   transIt.next }) */
      // assertion was sometimes violated even though both lists
      // had the same size
      reuseTransInfo(instance, transIt.next, mTransIt.next)
    }
  }

  def reuseTransInfo(instance: MBT, newTrans: Transition, master: Transition): Unit = {
    assert(
      (newTrans.origin.name.equals(master.origin.name)) &&
        (newTrans.dest.name.equals(master.dest.name)),
      { newTrans.toString + " does not match " + master.toString }
    )
    newTrans.origin = master.origin
    newTrans.dest = master.dest
    newTrans.coverage = master.coverage
    newTrans.averageReward = master.averageReward // averageReward of the transition - Rui
    newTrans.n = master.n
    assert(
      newTrans.isSynthetic == master.isSynthetic, {
        newTrans.toString + ".isSynthetic == " + newTrans.isSynthetic +
          ", but " + master.toString + ".isSynthetic == " + master.isSynthetic
      }
    )
    if (newTrans.isSynthetic) {
      return
    }
    reuseOverrideInfo(instance,
                      newTrans.nextStatePredicates,
                      master.nextStatePredicates)
    reuseOverrideInfo(instance,
                      newTrans.nonDetExceptions,
                      master.nonDetExceptions)
  }

  def reuseOverrideInfo(instance: MBT,
                        target: List[NextStateOverride],
                        source: List[NextStateOverride]): Unit = {
    val sourceIt = source.iterator
    val targetIt = target.iterator
    while (sourceIt.hasNext) {
      assert(targetIt.hasNext)
      val t1 = sourceIt.next
      val t2 = targetIt.next
      assert(t1.target.isSynthetic)
      assert(t2.target.isSynthetic)
      reuseTransInfo(instance, t2.target, t1.target)
      instance.transitions += t2.target
    }
  }

  def prep(t: Transition): Unit = {
    t.coverage.precond.count = 0
  }

  def precond(outcome: Boolean): Unit = {
    val t = MBT.currentTransition
    val pCov = t.coverage.precond
    val pCount = t.coverage.expectedReward
    val c = pCov.count
    if (outcome) {
      pCov.precondPassed.set(c)
      // todo: update procondPassed counter -rui
      pCount.updatePrecondPassededCounter
    } else {
      pCov.precondFailed.set(c)
      // todo: update procondFailed counter -rui
      pCount.updatePrecondFailedCounter
    }
    pCov.count = c + 1
  }

  // todo: count assert -Rui
  def assertCount(assertion: Boolean): Unit = {
    val t = MBT.currentTransition
    val aCount = t.coverage.expectedReward
    if (!assertion)
      aCount.updateAssertFailedCounter
    else
      aCount.updateAssertPassedCounter
  }

}

class TransitionCoverage {
  var count = 0
  val precond = new PreconditionCoverage
  // todo: expected reward of transition -Rui
  val expectedReward = new TransitionExpectedReward

  def cover: Unit = {
    count += 1
  }

  def isCovered = (count != 0)
}
