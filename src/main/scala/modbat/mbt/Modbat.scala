package modbat.mbt

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.net.URL
import java.util.BitSet

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import modbat.cov.{StateCoverage, TransitionCoverage, Trie}
import modbat.dsl.Action
import modbat.dsl.Init
import modbat.dsl.Shutdown
import modbat.dsl.State
import modbat.dsl.Transition
import modbat.log.Log
import modbat.trace._
import modbat.util.CloneableRandom
import modbat.util.SourceInfo
import modbat.util.FieldUtil

/** Contains code to explore model */
object Modbat {

  object AppState extends Enumeration {
    val AppExplore, AppShutdown = Value
  }

  import AppState._

  val origOut = Console.out
  val origErr = Console.err
  var out: PrintStream = origOut
  var err: PrintStream = origErr
  var logFile: String = _
  var errFile: String = _
  var failed = 0
  var count = 0
  val firstInstance = new LinkedHashMap[String, MBT]()
  var appState = AppExplore // track app state in shutdown handler
  // shutdown handler is registered at time when model exploration starts
  private var executedTransitions = new ListBuffer[RecordedTransition]
  private var randomSeed: Long = 0 // current random seed
  val masterRNG: CloneableRandom = MBT.rng.asInstanceOf[CloneableRandom].clone
  private val timesVisited = new HashMap[RecordedState, Int]
  val testFailures =
    new HashMap[(TransitionResult, String), ListBuffer[Long]]()

  // TODO: The trie for putting executed transtions paths -Rui
  var trie = new Trie()
  // TODO: Listbuffer to store a tuple: <ModelName, ModelIndex, transition> = [String, Int, Transition] -Rui
  //private var pathInfoRecorder = new ListBuffer[(String, Int, Transition)]
  private var pathInfoRecorder = new ListBuffer[PathInfo]

  def init {
    if (Main.config.init) {
      MBT.invokeAnnotatedStaticMethods(classOf[Init], null)
    }
  }

  def shutdown {
    if (Main.config.shutdown) {
      MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
    }
  }

  def showFailure(f: (TransitionResult, String)) = {
    val failureType = f._1
    val failedTrans = f._2
    assert(TransitionResult.isErr(failureType))
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

  def warnPrecond(modelInst: MBT, t: Transition, idx: Int) {
    Log.info(
      "Precondition " + (idx + 1) + " always " +
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
    Log.info("Hello")
    // TODO: display path coverage
    // Display executed transitions paths in graphs
    // if the configuration of path coverage is true -Rui
    if (Main.config.dotifyPathCoverage) {
      trie.display(trie.root)
      val numOfPaths = trie.numOfPaths(trie.root)
      Log.info(numOfPaths + " paths executed.")
      new PathInPointGraph(trie, "Point").dotify()
      new PathInBoxGraph(trie, "Box").dotify()
    }

    Log.info(
      count + " tests executed, " + (count - failed) + " ok, " +
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
      Log.info(
        modelStr + nCoveredStates + " states covered (" +
          nCoveredStates * 100 / nStates + " % out of " + nStates + "),")
      Log.info(
        modelStr + nCoveredTrans + " transitions covered (" +
          nCoveredTrans * 100 / nTrans + " % out of " + nTrans + ").")
    }
    preconditionCoverage
    randomSeed = (masterRNG.z << 32 | masterRNG.w)
    Log.info("Random seed for next test would be: " + randomSeed.toHexString)
    if (Main.config.dotifyCoverage) {
      for ((modelName, modelInst) <- firstInstance) {
        new Dotify(modelInst, modelName + ".dot").dotify(true)
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
    restoreChannel(out, origOut, logFile)
    restoreChannel(err, origErr, errFile, true)
  }

  def restoreChannel(ch: PrintStream,
                     orig: PrintStream,
                     filename: String,
                     isErr: Boolean = false) {
    if (Main.config.redirectOut) {
      ch.close()
      val file = new File(filename)
      if ((Main.config.deleteEmptyLog && (file.length == 0)) ||
          (Main.config.removeLogOnSuccess && !MBT.testHasFailed)) {
        if (!file.delete()) {
          Log.warn("Cannot delete file " + filename)
        }
      }
      if (isErr) {
        System.setErr(orig)
        Log.err = orig
      } else {
        System.setOut(orig)
        Console.print("[2K\r")
        Log.log = orig
      }
    }
  }

  def explore(n: Int) = {
    init
    Runtime.getRuntime().addShutdownHook(ShutdownHandler)

    runTests(n)

    coverage
    appState = AppShutdown
    shutdown
    Runtime.getRuntime().removeShutdownHook(ShutdownHandler)
  }

  def getRandomSeed = {
    val rng = MBT.rng.asInstanceOf[CloneableRandom]
    assert(rng.w <= 0xffffffffL)
    assert(rng.z <= 0xffffffffL)
    rng.z << 32 | rng.w
  }

  def wrapRun = {
    Console.withErr(err) {
      Console.withOut(out) {
        val model = MBT.launch(null)
        val result = exploreModel(model)
        MBT.cleanup()
        result
      }
    }
  }

  def runTest = {
    MBT.clearLaunchedModels
    MBT.testHasFailed = false
    wrapRun
  }

  def runTests(n: Int) {
    for (i <- 1 to n) { // n is the number of test cases
      MBT.rng = masterRNG.clone
      // advance RNG by one step for each path
      // so each path stays the same even if the length of other paths
      // changes due to small changes in the model or in this tool
      randomSeed = getRandomSeed
      val seed = randomSeed.toHexString
      failed match {
        case 0 => Console.printf("%8d %16s", i, seed)
        case 1 => Console.printf("%8d %16s, one test failed.", i, seed)
        case _ => Console.printf("%8d %16s, %d tests failed.", i, seed, failed)
      }
      logFile = Main.config.logPath + "/" + seed + ".log"
      errFile = Main.config.logPath + "/" + seed + ".err"
      if (Main.config.redirectOut) {
        out = new PrintStream(new FileOutputStream(logFile))
        System.setOut(out)
        Log.log = out

        err = new PrintStream(new FileOutputStream(errFile), true)
        System.setErr(err)
        Log.err = err
      } else {
        Console.println
      }
      MBT.checkDuplicates = (i == 1)
      val result = runTest
      count = i
      restoreChannels
      if (TransitionResult.isErr(result)) {
        failed += 1
      } else {
        assert(result == Ok())
      }
      masterRNG.nextInt(false) // get one iteration in RNG
      if (TransitionResult.isErr(result) && Main.config.stopOnFailure) {
        return
      }
    }
  }

  def showTrans(t: RecordedTransition) = {
    if (t == null) {
      "(transition outside model such as callback)"
    } else {
      t.transition.ppTrans(true)
    }
  }

  def exploreModel(model: MBT) = {
    Log.debug("--- Exploring model ---")
    timesVisited.clear
    executedTransitions.clear
    pathInfoRecorder.clear() // TODO: clear path information - Rui
    timesVisited += ((RecordedState(model, model.initialState), 1))
    for (f <- model.tracedFields.fields) {
      val value = FieldUtil.getValue(f, model.model)
      Log.fine("Trace field " + f.getName + " has initial value " + value)
      model.tracedFields.values(f) = value
    }
    val result = exploreSuccessors
    val retVal = result._1
    val recordedTrans = result._2
    assert(retVal == Ok() || TransitionResult.isErr(retVal))
    MBT.testHasFailed = TransitionResult.isErr(retVal)
    if (TransitionResult.isErr(retVal)) {
      val entry = (retVal, showTrans(recordedTrans))
      val rseeds = testFailures.getOrElseUpdate(entry, new ListBuffer[Long]())
      rseeds += randomSeed
    }
    // TODO: classify errors
    Log.debug("--- Resetting to initial state ---")
    retVal
  }

  def addSuccessors(m: MBT,
                    result: ArrayBuffer[(MBT, Transition)],
                    quiet: Boolean = false) {
    for (s <- m.successors(quiet)) {
      if (!quiet) {
        Log.debug(
          "State " + s.dest +
            " in model " + m.name + " was visited " +
            timesVisited.getOrElseUpdate(RecordedState(m, s.dest), 0)
            + " times.")
      }
      val limit = Main.config.loopLimit
      if ((limit != 0) &&
          (timesVisited.getOrElseUpdate(RecordedState(m, s.dest), 0)
            >= limit)) {
        if (!quiet) {
          Log.fine(
            "Detected beginning of loop " + limit +
              " (model " + m.name + ", state " + s.dest +
              "), filtering transition " + s + ".")
        }
      } else {
        val succ = (m, s)
        result += succ
      }
    }
  }

  def allSuccessors(givenModel: MBT) = {
    var result = new ArrayBuffer[(MBT, Transition)]()
    if (givenModel == null) {
      for (m <- MBT.launchedModels filterNot (_ isObserver)
             filter (_.joining == null)) {
        addSuccessors(m, result)
      }
    } else {
      if (givenModel.joining == null) {
        addSuccessors(givenModel, result)
      }
    }
    result.toArray
  }

  def totalWeight(trans: Array[(MBT, Transition)]) = {
    var w = 0.0
    for (t <- trans) {
      w = w + t._2.action.weight
    }
    w
  }

  def weightedChoice(choices: Array[(MBT, Transition)], totalW: Double) = {
    val n = (totalW * MBT.rng.nextFloat(false))
    var w = choices(0)._2.action.weight
    var i = 0
    while (w < n) {
      i = i + 1
      w = w + choices(i)._2.action.weight
    }
    choices(i)
  }

  def updateExecHistory(model: MBT,
                        localStoredRNGState: CloneableRandom,
                        result: (TransitionResult, RecordedTransition),
                        updates: List[(Field, Any)]) {
    result match {
      case (Ok(_), successorTrans: RecordedTransition) =>
        successorTrans.updates = updates
        successorTrans.randomTrace = MBT.rng.asInstanceOf[CloneableRandom].trace
        successorTrans.debugTrace =
          MBT.rng.asInstanceOf[CloneableRandom].debugTrace

        // TODO: get recorded choices - Rui
        successorTrans.recordedChoices =
          MBT.rng.asInstanceOf[CloneableRandom].getRecordedChoices()

        MBT.rng.asInstanceOf[CloneableRandom].clear
        executedTransitions += successorTrans
        val timesSeen =
          timesVisited.getOrElseUpdate(
            RecordedState(model, successorTrans.dest),
            0)
        timesVisited += ((RecordedState(model, successorTrans.dest),
                          timesSeen + 1))
      case (Backtrack, backTrackedTrans: RecordedTransition) =>
        //TODO: get recorded choices for backtracked trans -Rui
        backTrackedTrans.recordedChoices =
          MBT.rng.asInstanceOf[CloneableRandom].getRecordedChoices()

        MBT.rng = localStoredRNGState // backtrack RNG state
      // retry with other successor states in next loop iteration
      case (r: TransitionResult, failedTrans: RecordedTransition) =>
        assert(TransitionResult.isErr(r))
        failedTrans.randomTrace = MBT.rng.asInstanceOf[CloneableRandom].trace
        failedTrans.debugTrace =
          MBT.rng.asInstanceOf[CloneableRandom].debugTrace
        failedTrans.recordedChoices = MBT.rng
          .asInstanceOf[CloneableRandom]
          .getRecordedChoices() //TODO: get recorded choices for failed trans -Rui

        MBT.rng.asInstanceOf[CloneableRandom].clear
        executedTransitions += failedTrans
    }

  }

  def otherThreadFailed = {
    MBT.synchronized {
      if (MBT.testHasFailed) {
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
    var failed = false // TODO: failed case - Rui

    while (!successors.isEmpty && totalW > 0) {
      val localStoredRNGState = MBT.rng.asInstanceOf[CloneableRandom].clone

      if (MBT.rng.nextFloat(false) < Main.config.abortProbability) {
        Log.debug("Aborting...")
        // todo: insert pathInfo to trie before return - Rui
        if (Main.config.dotifyPathCoverage) trie.insert(pathInfoRecorder)
        return (Ok(), null)
      }

      val successor = weightedChoice(successors, totalW)
      val model = successor._1
      val trans = successor._2
      assert(!trans.isSynthetic)
      // TODO: Path coverage
      val result = model.executeTransition(trans)

      var updates: List[(Field, Any)] = Nil
      updates = model.tracedFields.updates
      for (u <- updates) {
        Log.fine("Trace field " + u._1 + " now has value " + u._2)
      }

      updateExecHistory(model, localStoredRNGState, result, updates)

      result match {
        case (Ok(sameAgain: Boolean), _) => {

          if (result._2.nextState != null) {
            Log.debug(
              "---print debug--- nextSate of transition: " + result._2.nextState.dest
                .toString()) // todo print debug
            Log.debug(
              "---print debug--- Current state of transition when nextState!=null: " + result._2.transition.dest
                .toString()) // todo print debug
          } else
            Log.debug(
              "---print debug--- Current state of transition when nextState is null: " + result._2.transition.dest
                .toString()) // todo print debug

          val succ = new ArrayBuffer[(MBT, Transition)]()
          addSuccessors(model, succ, true)
          if (succ.size == 0) {
            Log.debug("Model " + model.name + " has terminated.")
            // Unblock all models that are joining this one.
            for (m <- MBT.launchedModels filter (_.joining == model)) {
              m.joining = null
            }
          }
          if (otherThreadFailed) {
            return (ExceptionOccurred(MBT.externalException.toString), null)
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
            return (ExceptionOccurred(MBT.externalException.toString), null)
          }
          backtracked = false
          failed = false // TODO: failed case -Rui
          allSucc = successors.clone
        }
        case (Backtrack, _) => {
          successors = successors filterNot (_ == successor)
          backtracked = true
          failed = false // TODO: failed case -Rui
        }
        case (t: TransitionResult, _) => {
          assert(TransitionResult.isErr(t))
          printTrace(executedTransitions.toList)
          failed = true
          //return result //TODO: need to see how to record failed transitions
        }
      }

      // todo: Store path information and return if it is a failed case -Rui
      storePathInfo(result, successor, backtracked, failed)
      if (failed) return result

      totalW = totalWeight(successors)

    }

    // TODO: insert all path information of the current test in trie - Rui
    insertPathInfoInTrie()

    if (successors.isEmpty && backtracked) {
      for (succ <- allSucc) {
        Log.warn(
          "All preconditions false at transition " +
            ppTrans(new RecordedTransition(succ._1, succ._2)))
      }
      Log.warn("Maybe the preconditions are too strict?")
    }
    Log.debug("No more successors.")
    if ((MBT.launchedModels filter (_.joining != null)).size != 0) {
      Log.warn(
        "Deadlock: Some models stuck waiting for another model to finish.")
      for (m <- MBT.launchedModels filter (_.joining != null)) {
        val trans = (executedTransitions filter (_.model eq m)).last
        Log.warn(m.name + ": " + ppTrans(trans))
      }
    }
    Transition.pendingTransitions.clear
    // in case a newly constructed model was never launched
    return (Ok(), null)
  }

  private def storePathInfo(result: (TransitionResult, RecordedTransition),
                            successor: (MBT, Transition),
                            backtracked: Boolean,
                            failed: Boolean): Unit = {

    val model = successor._1
    val trans = successor._2

    // TODO: record choices in the current transition
    if (result._2.recordedChoices.nonEmpty)
      trans.recordedChoices = result._2.recordedChoices

    // TODO: Store path information -Rui
    // Store path information including the model name,
    // model ID and executed transition for path coverage,
    // if the configuration of path coverage is true. -Rui
    if (Main.config.dotifyPathCoverage) {
      if (backtracked) { // backtracked case
        // record next state into current transition,
        // when backtracked, the next state is the origin state
        trans.nextStateNextIf =
          trans.getNextStateNextIf(result._2.transition.origin, false)
        pathInfoRecorder += new PathInfo(model.className,
                                         model.mIdx,
                                         trans,
                                         TransitionQuality.backtrack)
      } else if (failed) { // failed case
        pathInfoRecorder += new PathInfo(model.className,
                                         model.mIdx,
                                         trans,
                                         TransitionQuality.fail)
        // TODO: add this failed transition to trie
        if (Main.config.dotifyPathCoverage) trie.insert(pathInfoRecorder)
        //return result // TODO: return
      } else { // success case
        // record next state into current transition.
        // next state is NOT null when result of "nextIf" condition is true,
        // record this next state, otherwise,
        // record the current transition's dest as the next state
        if (result._2.nextState != null) {
          trans.nextStateNextIf =
            trans.getNextStateNextIf(result._2.nextState.dest, true)
        } else {
          trans.nextStateNextIf =
            trans.getNextStateNextIf(result._2.transition.dest, false)
        }
        pathInfoRecorder += new PathInfo(model.className, model.mIdx, trans)
      }
    }
  }

  private def insertPathInfoInTrie(): Unit = {
    // TODO: output all executed transitions of the current test - Rui
    for (p <- pathInfoRecorder)
      Log.debug(
        "Recorded information for path coverage: " + p.toString + ", transID:" + p.transition.idx)
    // TODO: Put information in pathInfoRecoder to the trie -Rui
    // Insert all the information of the current test into a trie for path coverage,
    // if the configuration of path coverage is true. - Rui
    if (Main.config.dotifyPathCoverage) trie.insert(pathInfoRecorder)
  }

  def updateObservers: TransitionResult = {
    for (observer <- MBT.launchedModels filter (_ isObserver)) {
      assert(observer.isObserver)
      val observerResult = updateObserver(observer)
      if (TransitionResult.isErr(observerResult)) {
        return observerResult
      }
    }
    Ok()
  }

  /* Observer update. This is not an instance method in MBT because
     it is only used in online mode (observer transitions are also
     recorded and normally replayed in offline mode). */
  def updateObserver(observer: MBT): TransitionResult = {
    val observedStates = new HashSet[State]()
    var result: TransitionResult = Ok()
    while (!observedStates.contains(observer.currentState)) {
      observedStates += observer.currentState
      result = executeObserverStep(observer)
    }
    result
  }

  def executeObserverStep(observer: MBT): TransitionResult = {
    for (trans <- observer.successors(false)) {
      assert(!trans.isSynthetic)
      val localStoredRNGState = MBT.rng.asInstanceOf[CloneableRandom].clone
      val result = observer.executeTransition(trans)
      Modbat.updateExecHistory(observer, localStoredRNGState, result, Nil)
      if (TransitionResult.isErr(result._1)) {
        printTrace(executedTransitions.toList)
      }
      if (result._1 != Backtrack) {
        return result._1
      }
    }
    Ok() // ignore case where no transition executes
  }

  def sourceInfo(action: Action, recordedAction: StackTraceElement) = {
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
      assert(action.transfunc != null)
      SourceInfo.sourceInfo(action, false)
    }
  }

  def ppTrans(nModels: Int,
              transName: String,
              action: Action,
              recAction: StackTraceElement,
              modelName: String) = {
    val sourceInfoStr = sourceInfo(action, recAction)
    if (nModels > 1) {
      sourceInfoStr + ": " + modelName + ": " + transName
    } else {
      sourceInfoStr + ": " + transName
    }
  }

  def ppTrans(recTrans: RecordedTransition): String = {
    val transStr =
      ppTrans(MBT.launchedModels.size,
              recTrans.trans.ppTrans(true),
              recTrans.transition.action,
              recTrans.recordedAction,
              recTrans.model.name)
    if (Main.config.showChoices && recTrans.randomTrace != null &&
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
