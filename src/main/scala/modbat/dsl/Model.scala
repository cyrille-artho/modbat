package modbat.dsl

import modbat.mbt.MBT
import modbat.cov.TransitionCoverage
import modbat.RequirementFailedException
import modbat.trace.{BoolChoice, FuncChoice, NumChoice}

import scala.language.implicitConversions

object Model {
  // TODO: if not run in Modbat main thread, also handle assertion failure
  // by setting testFailed and triggering error trace display in Modbat
  // main loop
  // requires synchronization on a global lock
  def assert(assertion: Boolean, message: Any): Unit = {
    TransitionCoverage.assertCount(assertion) // update assertion counters -Rui
    if (!assertion) {
      // if in different thread, set testHasFailed
      // do not set this flag in Modbat thread as functions that are
      // expected to throw an assertion failure would otherwise mistakenly
      // mark a test as failed
      if (Thread.currentThread != MBT.modbatThread) {
        MBT.synchronized {
          val e = new AssertionError(
            "Assertion failed in Thread " +
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
}

abstract trait Model {
  def assert(assertion: Boolean): Unit = Model.assert(assertion, null)

  def assert(assertion: Boolean, message: Any): Unit =
    Model.assert(assertion, message)

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

  def maybe(action: Action) = MBT.maybe(action)

  // TODO: Currenty unused; remove?
  def maybeBool(pred: () => Boolean) = MBT.maybeBool(pred)

  implicit def transfuncToAction(action: => Any): Action = {
    new Action(() => action)
  }

  def skip {}

  def launch(modelInstance: Model) = MBT.launch(modelInstance)

  def join(modelInstance: Model) = efsm.join(modelInstance)

  def choose(min: Int, max: Int) = {
    val choice = MBT.choose(min, max)
    val numChoice = NumChoice(choice) // create a number choice -Rui
    MBT.rng.recordChoice(numChoice) // record number choice -Rui

    choice
  }

  def choose() = {
    val choice = (MBT.choose(0, 2) == 0)
    val boolChoice = BoolChoice(choice) // create a boolean choice -Rui
    MBT.rng.recordChoice(boolChoice) // record boolean choice -Rui

    choice
  }

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

  type AnyFunc = () => Any
  def choose(actions: AnyFunc*): Any = {
    val choice = MBT.rng.nextFunc(actions.toArray)
    val funcChoice = FuncChoice(choice) // create a func choice -Rui
    MBT.rng.recordChoice(funcChoice) // record func choice - Rui

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
