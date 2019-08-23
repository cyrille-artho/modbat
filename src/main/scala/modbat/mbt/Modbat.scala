package modbat.mbt

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.RuntimeException
import java.net.URL
import java.util.BitSet
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

import modbat.cov.StateCoverage
import modbat.dsl.Action
import modbat.dsl.Init
import modbat.dsl.Shutdown
import modbat.dsl.State
import modbat.dsl.Transition
import modbat.log.Log
import modbat.trace.Backtrack
import modbat.trace.ErrOrdering
import modbat.trace.ExceptionOccurred
import modbat.trace.ExpectedExceptionMissing
import modbat.trace.Ok
import modbat.trace.RecordedState
import modbat.trace.RecordedTransition
import modbat.trace.TransitionResult
import modbat.util.CloneableRandom
import modbat.util.SourceInfo
import modbat.util.FieldUtil

import com.miguno.akka.testing.VirtualTime

class NoTaskException(message :String = null, cause :Throwable = null) extends RuntimeException(message, cause)

/** Contains code to explore model */

/** Keep only system-wide settings that never change between executions here,
    to allow for running multiple instances in parallel for testing. */
object Modbat {
  val origOut = Console.out
  val origErr = Console.err
}

class Modbat(val config: Configuration) {
  object AppState extends Enumeration {
    val AppExplore, AppShutdown = Value
  }
  import AppState._
  val mbt = new MBT
  var out: PrintStream = Modbat.origOut
  var err: PrintStream = Modbat.origErr
  var logFile: String = _
  var errFile: String = _
  var failed = 0
  var count = 0
  val firstInstance = new LinkedHashMap[String, ModelInstance]()
  var appState = AppExplore // track app state in shutdown handler
  // shutdown handler is registered at time when model exploration starts
  private var executedTransitions = new ListBuffer[RecordedTransition]
  private var randomSeed: Long = 0 // current random seed
  var masterRNG: CloneableRandom = _
  private val timesVisited = new HashMap[RecordedState, Int]
  val testFailures =
    new HashMap[(TransitionResult, String), ListBuffer[Long]]()
//  val time = new VirtualTime
  var isUnitTest = true
  mbt.init(this)

  def setup {
    masterRNG = mbt.rng.asInstanceOf[CloneableRandom].clone
    // call init if needed
    if (config.init) {
      mbt.invokeAnnotatedStaticMethods(classOf[Init], null)
    }
  }

  def shutdown {
    if (config.shutdown) {
      mbt.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
    }
  }

  def showFailure(f: (TransitionResult, String)) = {
    val failureType = f._1
    val failedTrans = f._2
    assert (TransitionResult.isErr(failureType))
    (failureType: @unchecked) match {
      case ExceptionOccurred(e) => e + " at " + failedTrans
      case ExpectedExceptionMissing =>
      "Expected exception did not occur at " + failedTrans
    }
  }

  def showErrors {
    if (testFailures.size < 1) {
      return
    }
    if (testFailures.size == 1) {
      Log.info("One type of test failure:")
    } else {
      Log.info(testFailures.size + " types of test failures:")
    }
    var i = 0
    for (f <- testFailures.keySet.toSeq.sortWith(ErrOrdering.lt)) {
      i += 1
      Log.info(i + ") " + showFailure(f) + ":")
      val rseeds = testFailures(f)
      Log.info("   " + rseeds.map(_.toHexString).mkString(" "))
    }
  }

  def passFailed(b: Boolean) = {
    if (b) {
      "passed"
    } else {
      "failed"
    }
  }

  def warnPrecond(modelInst: ModelInstance, t: Transition, idx: Int) {
    Log.info("Precondition " + (idx + 1) + " always " +
	     passFailed(t.coverage.precond.precondPassed.get(idx)) +
	     " at transition " +
	     ppTrans(new RecordedTransition(modelInst, t)))
  }

  def preconditionCoverage {
    for ((modelName, modelInst) <- firstInstance) {
      for (t <- modelInst.transitions) {
        val diffSet =
          t.coverage.precond.precondPassed.clone.asInstanceOf[BitSet]
        diffSet.xor(t.coverage.precond.precondFailed)
        var idx = diffSet.nextSetBit(0)
        while (idx != -1) {
          warnPrecond(modelInst, t, idx)
          if (t.coverage.precond.precondFailed.get(idx)) {
            idx = -1
          } else {
            idx = diffSet.nextSetBit(idx + 1)
          }
        }
      }
    }
  }

  def coverage {
    Log.info(count + " tests executed, " + (count - failed) + " ok, " +
	     failed + " failed.")
    if (count == 0) {
      return
    }
    showErrors
    for ((modelName, modelInst) <- firstInstance) {
      val nCoveredStates =
	      (modelInst.states.values filter (_.coverage.isCovered)).size
      val nCoveredTrans =
	      (modelInst.transitions filter (_.coverage.isCovered)).size
      var modelStr = ""
      if (firstInstance.size != 1) {
	      modelStr = modelName + ": "
      }
      val nStates = modelInst.states.size
      val nTrans = modelInst.transitions.size
      Log.info(modelStr + nCoveredStates + " states covered (" +
	       nCoveredStates * 100 / nStates + " % out of " + nStates + "),")
      Log.info(modelStr + nCoveredTrans + " transitions covered (" +
	       nCoveredTrans * 100 / nTrans + " % out of " + nTrans + ").")
    }
    preconditionCoverage
    randomSeed = (masterRNG.z << 32 | masterRNG.w)
    Log.info("Random seed for next test would be: " + randomSeed.toHexString)
    if (config.dotifyCoverage) {
      for ((modelName, modelInst) <- firstInstance) {
	      new Dotify(config, modelInst, modelName + ".dot").dotify(true)
      }
    }
  }

  object ShutdownHandler extends Thread {
    override def run() {
      if (appState == AppExplore) {
        restoreChannels
        Console.println
        coverage
      }
    }
  }

  def restoreChannels {
    restoreChannel(out, Modbat.origOut, logFile)
    restoreChannel(err, Modbat.origErr, errFile, true)
  }

  def restoreChannel(ch: PrintStream, orig: PrintStream,
  filename: String, isErr: Boolean = false) {
    if (config.redirectOut) {
      ch.close()
      val file = new File(filename)
      if ((config.deleteEmptyLog && (file.length == 0)) ||
      (config.removeLogOnSuccess && !mbt.testHasFailed)) {
        if (!file.delete()) {
          Log.warn("Cannot delete file " + filename)
        }
      }
      if (isErr) {
        System.setErr(orig)
      } else {
        System.setOut(orig)
        Console.print("[2K\r")
      }
    }
  }

  def explore(n: Int) {
    if (!isUnitTest) {
      Runtime.getRuntime().addShutdownHook(ShutdownHandler)
    }

    runTests(n)

    coverage
    appState = AppShutdown
    shutdown
    Runtime.getRuntime().removeShutdownHook(ShutdownHandler)
  }

  def getRandomSeed = {
    val rng = mbt.rng.asInstanceOf[CloneableRandom]
    assert (rng.w <= 0xffffffffL)
    assert (rng.z <= 0xffffffffL)
    rng.z << 32 | rng.w
  }

  def wrapRun = {
    Console.withErr(err) {
      Console.withOut(out) {
	 val model = mbt.launch(null)
	 val result = exploreModel(model)
	 mbt.cleanup()
	 result
      }
    }
  }

  def runTest = {
    mbt.clearLaunchedModels
    mbt.testHasFailed = false
    wrapRun
  }

  def runTests(n: Int) {
   for (i <- 1 to n) {
      mbt.rng = masterRNG.clone
      // advance RNG by one step for each path
      // so each path stays the same even if the length of other paths
      // changes due to small changes in the model or in this tool
      randomSeed = getRandomSeed
      val seed = randomSeed.toHexString
      failed match {
        case 0 => Console.printf("%8d %16s", i, seed)
        case 1 => Console.printf("%8d %16s, one test failed.", i, seed)
        case _ => Console.printf("%8d %16s, %d tests failed.",
				i, seed, failed)
      }
      logFile = config.logPath + "/" + seed + ".log"
      errFile = config.logPath + "/" + seed + ".err"
      if (config.redirectOut) {
        out = new PrintStream(new FileOutputStream(logFile))
        System.setOut(out)

        err = new PrintStream(new FileOutputStream(errFile), true)
        System.setErr(err)
      } else {
	      Console.println
      }
      mbt.checkDuplicates = (i == 1)
      val result = runTest
      count = i
      restoreChannels
      if (TransitionResult.isErr(result)) {
	      failed += 1
      } else {
	      assert (result == Ok())
      }
      masterRNG.nextInt(false) // get one iteration in RNG
      if (TransitionResult.isErr(result) && config.stopOnFailure) {
	      return
      }
    }
  }

  def showTrans(t: RecordedTransition) = {
    if (t == null) {
      "(transition outside model such as callback)"
    } else {
      t.transition.ppTrans(config.autoLabels, true)
    }
  }

  def exploreModel(model: ModelInstance) = {
    Log.debug("--- Exploring model ---")
    timesVisited.clear
    executedTransitions.clear
    timesVisited += ((RecordedState(model, model.initialState), 1))
    for (f <- model.tracedFields.fields) {
      val value = FieldUtil.getValue(f, model.model)
      Log.fine("Trace field " + f.getName + " has initial value " + value)
      model.tracedFields.values(f) = value
    }
    val result = exploreSuccessors
    val retVal = result._1
    val recordedTrans = result._2
    assert (retVal == Ok() || TransitionResult.isErr(retVal))
    mbt.testHasFailed = TransitionResult.isErr(retVal)
    if (TransitionResult.isErr(retVal)) {
      val entry = (retVal, showTrans(recordedTrans))
      val rseeds = testFailures.getOrElseUpdate(entry, new ListBuffer[Long]())
      rseeds += randomSeed
    }
    // TODO: classify errors
    Log.debug("--- Resetting to initial state ---")
    retVal
  }

  def addSuccessors(m: ModelInstance, result: ArrayBuffer[(ModelInstance, Transition)],
	quiet: Boolean = false) {
    for (s <- m.successors(quiet)) {
      if (!quiet) {
	      Log.debug("State " + s.dest +
		    " in model " + m.name + " was visited " +
		    timesVisited.getOrElseUpdate(RecordedState(m, s.dest), 0)
		    + " times.")
      }
      val limit = config.loopLimit
      if ((limit != 0) &&
        (timesVisited.getOrElseUpdate(RecordedState(m, s.dest), 0)
        >= limit)) {
        if (!quiet) {
          Log.fine("Detected beginning of loop " + limit + 
            " (model " + m.name + ", state " + s.dest +
            "), filtering transition " + s + ".")
        }
      } else {
        val succ = (m, s)
        result += succ
      }
    }
  }

  def allSuccessors(givenModel: ModelInstance): Array[(ModelInstance, Transition)] = {
    val result = new ArrayBuffer[(ModelInstance, Transition)]()
    if (givenModel == null) {
      mbt.stayLock.synchronized {
        // TODO: allow selection to be overridden by invokeTransition
        val (staying, notStaying) = mbt.launchedModels partition (_.staying)
        for (m <- notStaying filterNot (_ isObserver)
          filter (_.joining == null)) {
          addSuccessors(m, result)
        }
        if (result.isEmpty && !staying.isEmpty) {
          mbt.time.scheduler.timeUntilNextTask match {
            case Some(s) => mbt.time.advance(s)
            case None => throw new NoTaskException()
          }
          return allSuccessors(givenModel)
        }
      }
    } else {
      if (givenModel.joining == null) {
    /* No need to check the queue of "staying" tasks here because as
      of now, the model of which the next transition is used is only
      enforced in the case of an exception that overrides the default
      successor state, by using "catches".
      This branch is therefore not taken for "staying" tasks. */
        addSuccessors(givenModel, result)
      }
    }
    result.toArray
  }

  def totalWeight(trans: Array[(ModelInstance, Transition)]) = {
    var w = 0.0
    for (t <- trans) {
      w = w + t._2.action.weight
    }
    w
  }

  def weightedChoice(choices: Array[(ModelInstance, Transition)], totalW: Double) = {
    val n = (totalW * mbt.rng.nextFloat(false))
    var w = choices(0)._2.action.weight
    var i = 0
    while (w < n) {
      i = i + 1
      w = w + choices(i)._2.action.weight
    }
    choices(i)
  }

  def updateExecHistory(model: ModelInstance,
  localStoredRNGState: CloneableRandom,
  result: (TransitionResult, RecordedTransition),
  updates: List[(Field, Any)]) {
    result match {
      case (Ok(_), successorTrans: RecordedTransition) =>
        successorTrans.updates = updates
        successorTrans.randomTrace =
          mbt.rng.asInstanceOf[CloneableRandom].trace
        successorTrans.debugTrace =
          mbt.rng.asInstanceOf[CloneableRandom].debugTrace
        mbt.rng.asInstanceOf[CloneableRandom].clear
        executedTransitions += successorTrans
        val timesSeen =
          timesVisited.getOrElseUpdate(RecordedState(model,
                      successorTrans.dest), 0)
        timesVisited += ((RecordedState(model, successorTrans.dest),
              timesSeen + 1))
      case (Backtrack, _) =>
        mbt.rng = localStoredRNGState // backtrack RNG state
        // retry with other successor states in next loop iteration
      case (r: TransitionResult, failedTrans: RecordedTransition) =>
        assert(TransitionResult.isErr(r))
        failedTrans.randomTrace =
          mbt.rng.asInstanceOf[CloneableRandom].trace
        failedTrans.debugTrace =
          mbt.rng.asInstanceOf[CloneableRandom].debugTrace
        mbt.rng.asInstanceOf[CloneableRandom].clear
        executedTransitions += failedTrans
    }
  }

  def otherThreadFailed = {
    mbt.synchronized {
      if (mbt.testHasFailed) {
        printTrace(executedTransitions.toList)
        true
      } else {
	      false
      }
    }
  }

  def exploreSuccessors: (TransitionResult, RecordedTransition) = {
    var successors = allSuccessors(null)
    var allSucc = successors.clone
    var totalW = totalWeight(successors)
    var backtracked = false
    while (!successors.isEmpty && (totalW > 0 || !mbt.transitionQueue.isEmpty)) {
      /* Pop invokeTransition queue until a feasible transition is popped.
       * If there is, execute it.
       * Otherwise, if total weight > 0, choose one transition by weight and execute it. */
      val localStoredRNGState = mbt.rng.asInstanceOf[CloneableRandom].clone

      if (mbt.rng.nextFloat(false) < config.abortProbability) {
        Log.debug("Aborting...")
        return (Ok(), null)
      }

      //invokeTransition
      var successor: (ModelInstance, Transition) = null
      if(!mbt.transitionQueue.isEmpty) Log.debug("Current InvokeTransitionQueue = (" + mbt.transitionQueue.mkString + ")")

      while (!mbt.transitionQueue.isEmpty && successor == null) {
        val (model, label) = mbt.transitionQueue.dequeue
        val trs = model.transitions.filter(_.action.label == label)
          .filter(_.origin == model.currentState)
        if(trs.size != 1) {
          Log.warn(s"${label} matches ${trs.size} transitions")
        } else {
          successor = (model, trs.head)
        }
      }
      if (successor == null && totalW > 0) {
        successor = weightedChoice(successors, totalW)
      }
      if(successor != null) {
        val model = successor._1
        val trans = successor._2
        assert (!trans.isSynthetic)
        // TODO: Path coverage
        val result = model.executeTransition(trans)
        var updates: List[(Field, Any)] = Nil
        updates  = model.tracedFields.updates
        for (u <- updates) {
	        Log.fine("Trace field " + u._1 + " now has value " + u._2)
        }
        updateExecHistory(model, localStoredRNGState, result, updates)
        result match {
          case (Ok(sameAgain: Boolean), _) => {
            val succ = new ArrayBuffer[(ModelInstance, Transition)]()
            addSuccessors(model, succ, true)
            if (succ.size == 0) {
              Log.debug("Model " + model.name + " has terminated.")
              // Unblock all models that are joining this one.
              for (m <- mbt.launchedModels filter (_.joining == model)) {
                m.joining = null
              }
            }
            if (otherThreadFailed) {
              return (ExceptionOccurred(mbt.externalException.toString), null)
            }
            if (sameAgain) {
              successors = allSuccessors(model)
            } else {
              successors = allSuccessors(null)
            }
            val observerResult = updateObservers
            if (TransitionResult.isErr(observerResult)) {
              return (observerResult, result._2)
            }
            if (otherThreadFailed) {
              return (ExceptionOccurred(mbt.externalException.toString), null)
            }
            backtracked = false
            allSucc = successors.clone
          }
          case (Backtrack, _) => {
            successors = successors filterNot (_ == successor)
            backtracked = true
          }
          case (t: TransitionResult, _) => {
            assert(TransitionResult.isErr(t))
            printTrace(executedTransitions.toList)
            return result
          }
        }
        totalW = totalWeight(successors)
      }
    }
    if (successors.isEmpty && backtracked) {
      for (succ <- allSucc) {
	      Log.warn("All preconditions false at transition " +
		    ppTrans(new RecordedTransition(succ._1, succ._2)))
      }
      Log.warn("Maybe the preconditions are too strict?")
    }
    Log.debug("No more successors.")
    if ((mbt.launchedModels filter (_.joining != null)).size != 0) {
      Log.warn("Deadlock: Some models stuck waiting for another model to finish.")
      for (m <- mbt.launchedModels filter (_.joining != null)) {
        val trans = (executedTransitions filter (_.model eq m)).last
        Log.warn(m.name + ": " + ppTrans(trans))
      }
    }
    Transition.pendingTransitions.clear
    // in case a newly constructed model was never launched
    return (Ok(), null)
  }

  def updateObservers: TransitionResult = {
    for (observer <- mbt.launchedModels filter (_ isObserver)) {
      assert(observer.isObserver)
      val observerResult = updateObserver(observer)
      if (TransitionResult.isErr(observerResult)) {
        return observerResult
      }
    }
    Ok()
  }

  /* Observer update. This is not an instance method in ModelInstance because
     it is only used in online mode (observer transitions are also
     recorded and normally replayed in offline mode). */
  def updateObserver(observer: ModelInstance): TransitionResult = {
    val observedStates = new HashSet[State]()
    var result: TransitionResult = Ok()
    while (!observedStates.contains(observer.currentState)) {
      observedStates += observer.currentState
      result = executeObserverStep(observer)
    }
    result
  }

  def executeObserverStep(observer: ModelInstance): TransitionResult = {
    for (trans <- observer.successors(false)) {
      assert (!trans.isSynthetic)
      val localStoredRNGState = mbt.rng.asInstanceOf[CloneableRandom].clone
      val result = observer.executeTransition(trans)
      updateExecHistory(observer, localStoredRNGState, result, Nil)
      if (TransitionResult.isErr(result._1)) {
	      printTrace(executedTransitions.toList)
      }
      if (result._1 != Backtrack) {
	      return result._1
      }
    }
    Ok() // ignore case where no transition executes
  }


  def sourceInfo(action: Action, recordedAction: StackTraceElement): String = {
    if (recordedAction != null) {
      val fullClsName = recordedAction.getClassName
      val idx = fullClsName.lastIndexOf('.')
      if (idx == -1) {
	      recordedAction.getFileName + ":" + recordedAction.getLineNumber
      } else {
        fullClsName.substring(0, idx + 1).replace('.', File.separatorChar) +
        recordedAction.getFileName + ":" + recordedAction.getLineNumber
      }
    } else {
      assert (action.transfunc != null)
      mbt.sourceInfo.sourceInfo(action, false)
    }
  }

  def ppTrans(nModels: Int, transName: String, action: Action,
	      recAction: StackTraceElement, modelName: String) = {
    val sourceInfoStr = sourceInfo(action, recAction)
    if (nModels > 1) {
      sourceInfoStr + ": " + modelName + ": " + transName
    } else {
      sourceInfoStr + ": " + transName
    }
  }

  def ppTrans(recTrans: RecordedTransition): String = {
    val transStr =
      ppTrans (mbt.launchedModels.size,
               recTrans.trans.ppTrans(config.autoLabels, true),
	       recTrans.transition.action,
	       recTrans.recordedAction, recTrans.model.name)
    if (config.showChoices && recTrans.randomTrace != null &&
	  recTrans.randomTrace.length != 0) {
      val choices = recTrans.debugTrace.mkString(", ")
      transStr + "; choices = (" + choices + ")"
    } else {
      transStr
    }
  }

  def printTrace(transitions: List[RecordedTransition]) {
    Log.warn("Error found, model trace:")
    for (t <- transitions) {
      Log.warn(ppTrans(t))
      for (u <- t.updates) {
	      Log.warn("  " + u._1 + " = " + u._2)
      }
    }
  }
}
