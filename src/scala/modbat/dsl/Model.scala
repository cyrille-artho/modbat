package modbat.dsl

import modbat.mbt.MBT
import modbat.cov.TransitionCoverage
import modbat.RequirementFailedException
import scala.language.implicitConversions

abstract trait Model {
  var efsm: MBT = null

  def getCurrentState = efsm.getCurrentState

  def getRandomSeed() = MBT.getRandomSeed

  def testFailed() = MBT.testFailed

  // delegate instance creation to MBT to distinguish between different
  // states with same name in different models
  implicit def stringPairToStatePair(names: (String, String)) = {
    new StatePair(new State(names._1), new State(names._2))
  }

  // allow sets of states for transitions with same pre-states
  // (common for transitions leading to same error state)
  implicit def multiTrans(names: (List[String], String)) = {
    new StateSet(names._1, names._2)
  }

  def maybe (action: Action) = MBT.maybe (action)

  // TODO: Currenty unused; remove?
  def maybeBool (pred: () => Boolean) = MBT.maybeBool (pred)

  implicit def transfuncToAction (action: => Any) : Action = {
    new Action(() => action)
  }

  def skip { }

  def launch(modelInstance: Model) = MBT.launch(modelInstance)

  def join(modelInstance: Model) = efsm.join(modelInstance)

  def choose(min: Int, max: Int) = MBT.choose(min, max)

  def choose() = (MBT.choose(0, 2) == 0)

  def require(requirement: Boolean, message: Any): Unit = {
    TransitionCoverage.precond(requirement)
    if (!requirement) {
      if (message == null) {
	throw new RequirementFailedException("requirement failed")
      } else {
	throw new RequirementFailedException("requirement failed: " + message)
      }
    }
  }

  def require(requirement: Boolean): Unit = require(requirement, null)

  // TODO: if not run in Modbat main thread, also handle assertion failure
  // by setting testFailed and triggering error trace display in Modbat
  // main loop
  // requires synchronization on a global lock
  def assert(assertion: Boolean, message: Any): Unit = {
    if (!assertion) {
      // if in different thread, set testHasFailed
      // do not set this flag in Modbat thread as functions that are
      // expected to throw an assertion failure would otherwise mistakenly
      // mark a test as failed
      if (Thread.currentThread != MBT.modbatThread) {
	MBT.synchronized {
	  val e = new AssertionError("Assertion failed in Thread " +
				     Thread.currentThread.getName)
	  MBT.externalException = e
	  MBT.testHasFailed = true
	}
      }
      if (message == null) {
	scala.Predef.assert(false)
      } else {
	scala.Predef.assert(false, message)
      }
    }
  }

  def assert(assertion: Boolean): Unit = assert(assertion, null)

  type AnyFunc = () => Any
  def choose(actions: AnyFunc*): Any = {
    val choice = MBT.rng.nextFunc(actions.toArray)
    choice()
  }

  type Predicate = () => Boolean
  def chooseIf(predActions: (Predicate, AnyFunc)*): Any = {
    val validChoices = predActions.filter(_._1())
    if (validChoices.size != 0) {
      val choice = choose(0, validChoices.size)
      validChoices(choice)._2()
    }
  }
}
