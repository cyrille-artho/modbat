package modbat.dsl

import modbat.mbt.MBT
import modbat.mbt.ModelInstance
import modbat.cov.TransitionCoverage
import modbat.RequirementFailedException
import scala.language.implicitConversions

abstract trait Model {
  def assert(assertion: Boolean): Unit = assert(assertion, null)

  def assert(assertion: Boolean, message: Any): Unit = {
    if (!assertion) {
      // if in different thread, set testHasFailed
      // do not set this flag in Modbat thread as functions that are
      // expected to throw an assertion failure would otherwise mistakenly
      // mark a test as failed
      if (Thread.currentThread != mbt.modbatThread) {
	mbt.synchronized {
	  val e = new AssertionError("Assertion failed in Thread " +
				     Thread.currentThread.getName)
	  mbt.externalException = e
	  mbt.testHasFailed = true
	}
      }
      if (message == null) {
	scala.Predef.assert(false)
      } else {
	scala.Predef.assert(false, message)
      }
    }
  }

  var efsm: ModelInstance = null
  var mbt: MBT = null

  def getCurrentState = efsm.getCurrentState

  def getRandomSeed() = mbt.getRandomSeed

  def testFailed() = mbt.testFailed

  // delegate instance creation to mbt to distinguish between different
  // states with same name in different models
  implicit def stringPairToStatePair(names: (String, String)) = {
    new StatePair(this, new State(names._1), new State(names._2))
  }

  // allow sets of states for transitions with same pre-states
  // (common for transitions leading to same error state)
  implicit def multiTrans(names: (List[String], String)) = {
    new StateSet(this, names._1, names._2)
  }

  def maybe (action: Action) = mbt.maybe (action)

  // TODO: Currenty unused; remove?
  def maybeBool (pred: () => Boolean) = mbt.maybeBool (pred)

  implicit def transfuncToAction (action: => Any) : Action = {
    new Action(this, () => action)
  }

  def skip { }

  def launch(modelInstance: Model) = mbt.launch(modelInstance)

  def join(modelInstance: Model) = efsm.join(modelInstance)

  def choose(min: Int, max: Int) = mbt.choose(min, max)

  def choose() = (mbt.choose(0, 2) == 0)

  def require(requirement: Boolean, message: Any): Unit = {
    TransitionCoverage.precond(mbt, requirement)
    if (!requirement) {
      if (message == null) {
	throw new RequirementFailedException("requirement failed")
      } else {
	throw new RequirementFailedException("requirement failed: " + message)
      }
    }
  }

  def require(requirement: Boolean): Unit = require(requirement, null)

  type AnyFunc = () => Any
  def choose(actions: AnyFunc*): Any = {
    val choice = actions(mbt.rng.nextInt(actions.size))
    val action = new Action(this, choice)
    mbt.rng.record(mbt.sourceInfo.actionInfo(action, true))
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

  def setWeight(label: String, weight: Double): Unit = {
    efsm.setWeight(label, weight)
  }
  def invokeTransition(label: String): Unit = {
    efsm.invokeTransition(label)
  }

}
