package modbat.mbt

import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.RuntimeException
import java.net.URL
import java.util.BitSet

import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.collection.mutable.ListBuffer
import modbat.cov.{
  StateCoverage,
  TransitionCoverage,
  TransitionRewardTypes,
  Trie
}
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

import scala.math._
import scala.util.Random
import com.miguno.akka.testing.VirtualTime

class NoTaskException(message: String = null, cause: Throwable = null)
    extends RuntimeException(message, cause)

/** Contains code to explore model */
object Modbat {
  object AppState extends Enumeration {
    val AppExplore, AppShutdown = Value
  }
  import AppState._

  var isUnitTest = true

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

  def passFailed(b: Boolean) = {
    if (b) {
      "passed"
    } else {
      "failed"
    }
  }

  def ppTrans(nModels: Int,
              transName: String,
              transition: Transition,
              recAction: StackTraceElement,
              modelName: String) = {
    if (nModels > 1) {
      transition.sourceInfo + ": " + modelName + ": " + transName
    } else {
      transition.sourceInfo + ": " + transName
    }
  }

  def restoreChannel(mbt: MBT,
                     ch: PrintStream,
                     orig: PrintStream,
                     filename: String,
                     isErr: Boolean = false): Unit = {
    if (mbt.config.redirectOut) {
      ch.close()
      val file = new File(filename)
      if ((mbt.config.deleteEmptyLog && (file.length == 0)) ||
          (mbt.config.removeLogOnSuccess && !mbt.testHasFailed)) {
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
}

class Modbat(val mbt: MBT) {
  import Modbat.AppState._
  val origOut = System.out
  val origErr = System.err
  var out: PrintStream = origOut
  var err: PrintStream = origErr
  var logFile: String = _
  var errFile: String = _
  var failed = 0
  var count = 0 // count the number of executed test cases.
  var appState = AppExplore // track app state in shutdown handler
  // shutdown handler is registered at time when model exploration starts
  private var executedTransitions = new ListBuffer[RecordedTransition]
  private var randomSeed: Long = 0 // current random seed
  var masterRNG: CloneableRandom = mbt.rng.asInstanceOf[CloneableRandom].clone
  private val timesVisited = new HashMap[RecordedState, Int]
  val testFailures =
    new HashMap[(TransitionResult, String), ListBuffer[Long]]()
//  val time = new VirtualTime

  // The trie to record sequences of executed transitions (execution paths) -Rui
  var trie = new Trie()
  private var pathInfoRecorder = new ListBuffer[PathInfo]

  if (mbt.config.init) {
    mbt.invokeAnnotatedStaticMethods(classOf[Init], null)
  }

  def shutdown: Unit = {
    if (mbt.config.shutdown) {
      mbt.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
    }
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
    mbt.clearLaunchedModels()
    mbt.testHasFailed = false
    wrapRun
  }

  def getRandomSeed = {
    val rng = mbt.rng.asInstanceOf[CloneableRandom]
    assert(rng.w <= 0xffffffffL)
    assert(rng.z <= 0xffffffffL)
    rng.z << 32 | rng.w
  }

  def restoreChannels: Unit = {
    Modbat.restoreChannel(mbt, out, origOut, logFile)
    Modbat.restoreChannel(mbt, err, origErr, errFile, true)
  }

  def showErrors: Unit = {
    if (testFailures.size < 1) {
      return
    }
    if (testFailures.size == 1) {
      Log.info("One type of test failure:")
    } else {
      Log.info(Integer.toString(testFailures.size) + " types of test failures:")
    }
    var i = 0
    for (f <- testFailures.keySet.toSeq.sortWith(ErrOrdering.lt)) {
      i += 1
      Log.info(Integer.toString(i) + ") " + Modbat.showFailure(f) + ":")
      val rseeds = testFailures(f)
      Log.info("   " + rseeds.map(_.toHexString).mkString(" "))
    }
  }

  def warnPrecond(modelInst: ModelInstance, t: Transition, idx: Int): Unit = {
    Log.info(
      "Precondition " + (idx + 1) + " always " +
        Modbat.passFailed(t.coverage.precond.precondPassed.get(idx)) +
        " at transition " +
        ppTrans(new RecordedTransition(modelInst, t)))
  }

  def preconditionCoverage: Unit = {
    for ((modelName, modelInst) <- mbt.firstInstance) {
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

  private def pathCoverageDisplay: Unit = {

    if (mbt.config.logLevel == Log.Debug) trie.display(trie.root)
    val numOfPaths = trie.numOfPaths(trie.root)
    Log.info(Integer.toString(numOfPaths) + " main paths executed.")
    val shortestPath = trie.shortestPath(trie.root)
    Log.info("the shortest path has " + shortestPath + " transitions.")
    val longestPath = trie.longestPath(trie.root)
    Log.info("the longest path has " + longestPath + " transitions.")
    // pathLengthResults is a map that records the all length of paths (keys),
    // and the number of the same length (values), based on the number of transitions
    val pathLengthResults = trie.pathLengthRecorder(trie.root)
    Log.info("path length results: " + pathLengthResults)
    // the average length of all paths, based on the number of transitions
    val averageLength = pathLengthResults.foldLeft(0.0) {
      case (a, (k, v)) => a + k * v
    } / numOfPaths
    Log.info("the average length of paths is: " + averageLength)
    // the standard deviation for the length of paths, based on the number of transitions
    val stdDev = math.sqrt(pathLengthResults.foldLeft(0.0) {
      case (a, (k, v)) => a + math.pow(k - averageLength, 2) * v
    } / numOfPaths)
    Log.info("the standard deviation for the length of paths is: " + stdDev)
    // information for the path based graph
    val (numNodePG,
         numChoiceNodePG,
         numBacktrackedEdgePG,
         numFailedEdgePG,
         numNonChoiceEdgePG,
         numChoiceEdgePG,
         numCycleSelfTranPG) =
      new PathInPointGraph(trie.root, "Point", "root", mbt).dotify()

    Log.info(
      "the total number of nodes in path-based graph:" + (numNodePG + numChoiceNodePG))
    Log.info(
      "the total number of choice nodes in path-based graph: " + numChoiceNodePG)

    Log.info(
      "the total number of edges in path-based graph: " + (numNonChoiceEdgePG + numChoiceEdgePG))

    Log.info(
      "the total number of non choice related edges in path-based graph: " + numNonChoiceEdgePG)

    Log.info(
      "the total number of backtracked edges in path-based graph: " + numBacktrackedEdgePG)
    Log.info(
      "the total number of failed edges in path-based graph: " + numFailedEdgePG)

    Log.info(
      "the total number of choice related edges in path-based graph: " + numChoiceEdgePG)
    Log.info(
      "the total number of cycles in path-based graph: " + numCycleSelfTranPG)

    Log.info(
      "---------------------- table data of PG -----------------------\n" +
        "Name" + " & " + "Test Cases" + " & " +
        "Nodes" + " & " + "Choice Nodes" + " & " + "Edges" + " & " + "failEdges" + " & " + "Cycles" + " & " +
        "LIP" + " & " + "LP" + " & " + "SP" + " & " + "AVE" + " & " + "SD" + "\n" +
        mbt.modelClass.getName + " & " + count + " & " +
        (numNodePG + numChoiceNodePG) + "  & " + numChoiceNodePG + "  & " + (numNonChoiceEdgePG + numChoiceEdgePG) +
        "   & " + numFailedEdgePG + "         & " + numCycleSelfTranPG + "    & " +
        numOfPaths + " & " + longestPath + " & " + shortestPath + "  & " + averageLength + " & " + stdDev + "\n" +
        "--------------------------------------------------------------"
    )
    // information for the state based graph
    val (numJumpedEdge,
         numChoiceNodeSG,
         numBacktrackedEdgeSG,
         numFailedEdgeSG,
         numNonChoiceEdgeSG,
         numChoiceEdgeSG,
         numCycleSelfTranSG) =
      new PathInStateGraph(trie.root, "State", "root", mbt).dotify()

    Log.info(
      "the total number of choice nodes in state-based graph: " + numChoiceNodeSG)

    Log.info(
      "the total number of edges in state-based graph: " + (numNonChoiceEdgeSG + numChoiceEdgeSG + numJumpedEdge))

    Log.info(
      "the total number of non choice related edges in state-based graph: " + numNonChoiceEdgeSG)

    Log.info(
      "the total number of backtracked edges in state-based graph: " + numBacktrackedEdgeSG)
    Log.info(
      "the total number of failed edges in state-based graph: " + numFailedEdgeSG)

    Log.info(
      "the total number of choice related edges in state-based graph: " + numChoiceEdgeSG)

    Log.info(
      "the total number of Jumped dotted edges in state-based graph: " + numJumpedEdge)

    Log.info(
      "the total number of cycles in state-based graph: " + numCycleSelfTranSG)

    Log.info(
      "---------------------- table data of SG -----------------------\n" +
        "Name" + " & " + "Test Cases" + " & " +
        "Choice Nodes" + " & " + "Edges" + " & " + "failEdges" + " & " + "Cycles" + "\n" +
        mbt.modelClass.getName + " & " + count + " & " +
        numChoiceNodeSG + " & " + (numNonChoiceEdgeSG + numChoiceEdgeSG + numJumpedEdge) +
        " & " + numFailedEdgeSG + " & " + numCycleSelfTranSG + "\n" +
        "--------------------------------------------------------------"
    )
  }

  private def pathCoverageBFSearch: Unit = {
    // bfsearch a recorded transition in the trie by using a string format,
    // key-level-parentNodeTranID as the input from terminal, where
    // key is the concatenation of the target transition's ID and quality(action outcome);
    // level is the level of the target transition recorded in the trie initialized from 0 level
    // parentNodeTranID is the target transition's parent transition's ID
    var input: String = ""
    import scala.io.StdIn.readLine
    while (input != "quit") {
      input = readLine()
      if (input != "quit") {
        val goal = input.split("-")
        val foundNode =
          trie.bfSearchT(trie.root, goal(0), goal(1).toInt, goal(2).toInt)
        if (foundNode.isLeaf)
          Log.debug(
            "the found transition is recorded in a leaf of the trie, so there is no children to print in graphs.")
        else {
          trie.display(foundNode)
          new PathInStateGraph(foundNode, "State", input, mbt).dotify()
          new PathInPointGraph(foundNode, "Point", input, mbt).dotify()
        }
      }
    }
  }

  def coverage: Unit = {

    if (mbt.config.dotifyPathCoverage) {
      pathCoverageDisplay // Display path coverage/execution paths in state and path graphs -Rui
      if (mbt.config.bfsearchFun)
        pathCoverageBFSearch // User search function to find a transition in trie as a starting point to display in graphs
    }

    Log.info(Integer.toString(count) + " tests executed, " +
             Integer.toString(count - failed) + " ok, " +
             Integer.toString(failed) + " failed.")
    if (count == 0) {
      return
    }
    showErrors
    for ((modelName, modelInst) <- mbt.firstInstance) {
      val nCoveredStates =
        (modelInst.states.values filter (_.coverage.isCovered)).size
      val nCoveredTrans =
        (modelInst.transitions filter (_.coverage.isCovered)).size
      var modelStr = ""
      if (mbt.firstInstance.size != 1) {
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
    if (mbt.config.dotifyCoverage) {
      for ((modelName, modelInst) <- mbt.firstInstance) {
        new Dotify(mbt.config, modelInst, modelName + ".dot").dotify(true)
      }
    }
  }

  object ShutdownHandler extends Thread {
    override def run(): Unit = {
      if (appState == AppExplore) {
        restoreChannels
        Console.println()
        coverage
      }
    }
  }

  def explore(n: Int): Unit = {
    if (!Modbat.isUnitTest) {
      Runtime.getRuntime().addShutdownHook(ShutdownHandler)
    }

    runTests(n)

    coverage
    appState = AppShutdown
    shutdown
    Runtime.getRuntime().removeShutdownHook(ShutdownHandler)
  }

  def runTests(n: Int): Unit = {
    for (i <- 1 to n) { // n is the number of test cases
      mbt.rng = masterRNG.clone
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
      logFile = mbt.config.logPath + "/" + seed + ".log"
      errFile = mbt.config.logPath + "/" + seed + ".err"
      if (mbt.config.redirectOut) {
        out = new PrintStream(new FileOutputStream(logFile))
        System.setOut(out)

        err = new PrintStream(new FileOutputStream(errFile), true)
        System.setErr(err)
      } else {
        Console.println()
      }
      mbt.checkDuplicates = (i == 1)
      val result = runTest
      count = i
      restoreChannels
      if (TransitionResult.isErr(result)) {
        failed += 1
      } else {
        assert(result == Ok())
      }
      masterRNG.nextInt(false) // get one iteration in RNG
      if (TransitionResult.isErr(result) && mbt.config.stopOnFailure) {
        return
      }
    }
  }

  def showTrans(t: RecordedTransition) = {
    if (t == null) {
      "(transition outside model such as callback)"
    } else {
      t.transition.ppTrans(mbt.config.autoLabels, true)
    }
  }

  def exploreModel(model: ModelInstance) = {
    Log.debug("--- Exploring model ---")
    timesVisited.clear()
    executedTransitions.clear()
    pathInfoRecorder.clear() // clear path information - Rui
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

  def addSuccessors(m: ModelInstance,
                    result: ListBuffer[(ModelInstance, Transition)],
                    quiet: Boolean = false): Unit = {
    for (s <- m.successors(quiet)) {
      if (!quiet) {
        Log.debug(
          "State " + s.dest +
            " in model " + m.name + " was visited " +
            timesVisited.getOrElseUpdate(RecordedState(m, s.dest), 0)
            + " times.")
      }
      val limit = mbt.config.loopLimit
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

  def allSuccessors(givenModel: ModelInstance): List[(ModelInstance, Transition)] = {
    val result = new ListBuffer[(ModelInstance, Transition)]()
    if (givenModel == null) {
      mbt.stayLock.synchronized {
        // TODO: allow selection to be overridden by invokeTransition
        val (staying, notStaying) = mbt.launchedModels partition (_.staying)
        for (m <- notStaying filterNot (_ isObserver)
               filter (_.joining == null)) {
          addSuccessors(m, result)
        }
        if (result.isEmpty && !staying.isEmpty) {
          mbt.time.scheduler.timeUntilNextTask() match {
            case Some(s) => mbt.time.advance(s)
            case None    => throw new NoTaskException()
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
    result.toList
  }

  def totalWeight(trans: List[(ModelInstance, Transition)]) = {
    var w = 0.0
    for (t <- trans) {
      w = w + t._2.action.weight
    }
    w
  }

  def makeChoice(choices: List[(ModelInstance, Transition)], totalW: Double) = {
    mbt.config.search match {
      case "random" => weightedChoice(choices, totalW)
      case "heur"   => heuristicChoice(choices, totalW)
    }
  }

  def heuristicChoice(choices: List[(ModelInstance, Transition)],
                      totalW: Double): (ModelInstance, Transition) = {
    // Compute choice based on bandit UCB and expected rewards (ER)  - Rui
    val choice = banditUCBERChoice(choices, totalW)
    choice
  }

  private def banditUCBERChoice(choices: List[(ModelInstance, Transition)],
                                totalW: Double): (ModelInstance, Transition) = {

    Log.debug("Tradeoff:" + mbt.config.banditTradeoff)
    Log.debug("Backtracked transition reward:" + mbt.config.backtrackTReward)
    Log.debug("Self-transition reward:" + mbt.config.selfTReward)
    Log.debug("Good transition reward:" + mbt.config.goodTReward)
    Log.debug("Failed transition reward:" + mbt.config.failTReward)

    Log.debug("Passed precondition reward:" + mbt.config.precondPassReward)
    Log.debug("Failed precondition reward:" + mbt.config.precondFailReward)
    Log.debug("Passed assertion reward:" + mbt.config.assertPassReward)
    Log.debug("Failed assertion reward:" + mbt.config.assertFailReward)

    val currentStateCount = choices.head._1.currentState.coverage.count
    val transCountLst = choices.map(_._2.coverage.count)
    val precondFailedCountLst =
      choices.map(_._2.coverage.expectedReward.countPrecondFail)

    Log.debug(
      "*** List of failed assertion counts:" + choices.map(
        _._2.coverage.expectedReward.countAssertFail))
    Log.debug(
      "*** List of passed assertion counts:" + choices.map(
        _._2.coverage.expectedReward.countAssertPass))

    Log.debug(
      "*** List of passed precondition counts:" + choices.map(
        _._2.coverage.expectedReward.countPrecondPass))

    Log.debug("*** List of failed precondition counts:" + precondFailedCountLst)
    Log.debug(
      "*** List of precond counters:" + choices.map(
        _._2.coverage.precond.count))

    val expectedRewardList =
      choices.map(_._2.coverage.expectedReward.expectedReward)
    Log.debug("*** List of expected reward:" + expectedRewardList)

    val rewardLst = choices.map(_._2.averageReward.rewardsLst)
    Log.debug("*** List of reward lists for transitions:" + rewardLst)

    val averageRewardLst = choices.map(_._2.averageReward.averageReward)
    Log.debug("*** List of average rewards for transitions:" + averageRewardLst)

    // nState is the total number of times for current state to be  visited already
    val nState = currentStateCount + precondFailedCountLst.sum
    Log.debug(
      "*** The total number of times for current state to be visited:" + nState)

    // nTranslst is the list to store all values of counters for already selected transitions
    val nTransLst = (transCountLst, precondFailedCountLst).zipped.map(_ + _)
    Log.debug(
      "*** The list to store all values of counters for already selected transitions:" + nTransLst)

    if (nTransLst.contains(0)) {
      // Choose an unselected transition when there are still unselected transitions
      return choices(nTransLst.indexOf(0)) //
    } else {
      // Compute choice based on the UCB formula of bandit problem
      val banditUCBSelectedTransLst =
        nTransLst.map(n => sqrt(mbt.config.banditTradeoff * log(nState) / n))
      Log.debug("*** banditUCBSelectedTransLst:" + banditUCBSelectedTransLst)

      // banditUCB is the sum of the average reward, less selected transition value, and expected reward
      val banditUCB =
        ((averageRewardLst, banditUCBSelectedTransLst).zipped.map(_ + _),
         expectedRewardList).zipped.map(_ + _)
      Log.debug("*** banditUCB:" + banditUCB)

      val banditUCBChoiceCandidates =
        banditUCB.zipWithIndex.filter(x => x._1 == banditUCB.max)
      Log.info("*** bandit UCB candidates:" + banditUCBChoiceCandidates)

      val banditUCBChoiceCandidate = banditUCBChoiceCandidates(
        mbt.rng.nextInt(banditUCBChoiceCandidates.length))
      val banditUCBChoiceIndex = banditUCBChoiceCandidate._2
      Log.info(
        "*** bandit UCB chosen candidate's index:" + banditUCBChoiceIndex)

      return choices(banditUCBChoiceIndex)
    }
  }

  def weightedChoice(choices: List[(ModelInstance, Transition)],
                     totalW: Double): (ModelInstance, Transition) = {

    val n = (totalW * mbt.rng.nextFloat(false))
    var w = 0.0
    for (c <- choices) {
      w = w + c._2.action.weight
      if (w >= n) {
        return c
      }
    }
    choices.last
  }

  def updateExecHistory(model: ModelInstance,
                        localStoredRNGState: CloneableRandom,
                        result: (TransitionResult, RecordedTransition),
                        updates: List[(Field, Any)]): Unit = {
    result match {
      case (Ok(_), successorTrans: RecordedTransition) =>
        successorTrans.updates = updates
        successorTrans.randomTrace = mbt.rng.asInstanceOf[CloneableRandom].trace()
        successorTrans.debugTrace =
          mbt.rng.asInstanceOf[CloneableRandom].debugTrace()

        // get recorded choices - Rui
        successorTrans.recordedChoices =
          mbt.rng.asInstanceOf[CloneableRandom].getRecordedChoices()

        mbt.rng.asInstanceOf[CloneableRandom].clear
        executedTransitions += successorTrans
        val timesSeen =
          timesVisited.getOrElseUpdate(
            RecordedState(model, successorTrans.dest),
            0)
        timesVisited += ((RecordedState(model, successorTrans.dest),
                          timesSeen + 1))
      case (Backtrack, backTrackedTrans: RecordedTransition) =>
        // get recorded choices for backtracked transition -Rui
        backTrackedTrans.recordedChoices =
          mbt.rng.asInstanceOf[CloneableRandom].getRecordedChoices()

        mbt.rng = localStoredRNGState // backtrack RNG state
      // retry with other successor states in next loop iteration
      case (r: TransitionResult, failedTrans: RecordedTransition) =>
        assert(TransitionResult.isErr(r))
        failedTrans.randomTrace = mbt.rng.asInstanceOf[CloneableRandom].trace()
        failedTrans.debugTrace =
          mbt.rng.asInstanceOf[CloneableRandom].debugTrace()
        failedTrans.recordedChoices = mbt.rng
          .asInstanceOf[CloneableRandom]
          .getRecordedChoices() // get recorded choices for failed transition -Rui

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

  def invocationSuccessor: Option[(ModelInstance, Transition)] = {
    if (!mbt.transitionQueue.isEmpty)
      Log.debug(
        "Current InvokeTransitionQueue = (" + mbt.transitionQueue.mkString + ")")

    while (!mbt.transitionQueue.isEmpty) {
      val (model, label) = mbt.transitionQueue.dequeue()
      val trs = model.transitions
        .filter(_.action.label == label)
        .filter(_.origin == model.currentState)
      if (trs.size != 1) {
        Log.warn(s"${label} matches ${trs.size} transitions")
      } else {
        return Some(model, trs.head)
      }
    }
    None
  }

  def checkForFieldUpdates(model: ModelInstance,
                           result: (TransitionResult, RecordedTransition),
                           rng: CloneableRandom): Unit = {
    val updates: List[(Field, Any)] = model.tracedFields.updates
    for (u <- updates) {
      Log.fine("Trace field " + u._1 + " now has value " + u._2)
    }
    updateExecHistory(model, rng, result, updates)
  }

  def unblockJoiningModels(model: ModelInstance): Unit = {
    // Unblock all models that are joining this one.
    for (m <- mbt.launchedModels filter (_.joining == model)) {
      m.joining = null
    }
  }

  def warnAboutPreconditions(allSucc: List[(ModelInstance, Transition)],
                             backtracked: Boolean): Unit = {
    for (succ <- allSucc) {
      Log.warn(
        "All preconditions false at transition " +
          ppTrans(new RecordedTransition(succ._1, succ._2)))
    }
    Log.warn("Maybe the preconditions are too strict?")
  }

  def checkIfPendingModels: Unit = {
    if ((mbt.launchedModels filter (_.joining != null)).size != 0) {
      Log.warn(
        "Deadlock: Some models stuck waiting for another model to finish.")
      for (m <- mbt.launchedModels filter (_.joining != null)) {
        val trans = (executedTransitions filter (_.model eq m)).last
        Log.warn(m.name + ": " + ppTrans(trans))
      }
    }
  }

  class PathResult(val result: (TransitionResult, RecordedTransition),
                   val successor: (ModelInstance, Transition),
                   val backtracked: Boolean,
                   val failed: Boolean,
                   val isObserver: Boolean)

  def exploreSuccessors: (TransitionResult, RecordedTransition) = {
    executeSuccessorTrans match {
      case ((Finished, _), _) => {
        insertPathInfoInTrie
        (Ok(), null)
      }
      case (result: (TransitionResult, RecordedTransition),
            pathResult: PathResult) => {
        if (!pathResult.isObserver) {
          storePathInfo(pathResult.result,
                        pathResult.successor,
                        pathResult.backtracked,
                        pathResult.failed)
        }
        result
      }
    }
  }

  def executeSuccessorTrans
    : ((TransitionResult, RecordedTransition), PathResult) = {
    var successors = allSuccessors(null)
    var allSucc = successors
    var totalW = totalWeight(successors)
    var backtracked = false // boolean var for backtracked case -Rui
    while (!successors.isEmpty && (totalW > 0 || !mbt.transitionQueue.isEmpty)) {
      val localStoredRNGState = mbt.rng.asInstanceOf[CloneableRandom].clone
      val abortProbability = mbt.rng.nextFloat(false)
      if (abortProbability < mbt.config.abortProbability) {
        Log.debug(
          "Configured abort probability:" + mbt.config.abortProbability)
        Log.debug("Actual abort probability:" + abortProbability)
        Log.debug("Aborting...")
        return ((Finished, null), null)
      }
      /* Pop invokeTransition queue until a feasible transition is popped.
       * If there is, execute it.
       * Otherwise, if total weight > 0, choose one transition by weight and execute it. */
      var successor: (ModelInstance, Transition) = null
      //successor = invocationSuccessor.getOrElse(weightedChoice(successors, totalW))
      // TODO: try bandit by calling makeChoice
      successor = invocationSuccessor.getOrElse(makeChoice(successors, totalW))
      if (successor != null) {
        val model = successor._1
        val trans = successor._2
        assert(!trans.isSynthetic)
        val result = model.executeTransition(trans)
        checkForFieldUpdates(model, result, localStoredRNGState)
        result match {
          case (Ok(sameAgain: Boolean), _) => {
            backtracked = false
            // todo: update the reward for the OK transition - Rui
            if (trans.origin == trans.dest) {
              trans.averageReward
                .updateAverageReward(TransitionRewardTypes.SelfTransReward)
            } else {
              trans.averageReward
                .updateAverageReward(TransitionRewardTypes.GoodTransReward)
            }

            val succ = new ListBuffer[(ModelInstance, Transition)]()
            addSuccessors(model, succ, true)
            if (succ.size == 0) {
              Log.debug("Model " + model.name + " has terminated.")
              unblockJoiningModels(model)
            }
            if (sameAgain) {
              successors = allSuccessors(model)
            } else {
              successors = allSuccessors(null)
            }
            val observerResult = updateObservers
            if (TransitionResult.isErr(observerResult)) {
              return ((observerResult, result._2),
                      new PathResult(result,
                                     successor,
                                     backtracked,
                                     true,
                                     true))
            }
            if (otherThreadFailed) {
              return ((ExceptionOccurred(mbt.externalException.toString), null),
                      new PathResult(result,
                                     successor,
                                     backtracked,
                                     true,
                                     false))
            }
            allSucc = successors
          }
          case (Backtrack, _) => {
            backtracked = true
            // todo: update the reward for the backtracked transition - Rui
            trans.averageReward
              .updateAverageReward(TransitionRewardTypes.BacktrackTransReward)
            successors = successors filterNot (_ == successor)
          }
          case (t: TransitionResult, _) => {
            // todo: update the reward for the failed transition - Rui
            trans.averageReward
              .updateAverageReward(TransitionRewardTypes.FailTransReward)
            assert(TransitionResult.isErr(t))
            printTrace(executedTransitions.toList)
            return (result,
                    new PathResult(result, successor, backtracked, true, false))
          }
        }
        storePathInfo(result, successor, backtracked, false)
        totalW = totalWeight(successors)
      }
    }
    if (successors.isEmpty && backtracked) {
      warnAboutPreconditions(allSucc, backtracked)
    }
    Log.debug("No more successors.")
    checkIfPendingModels
    ((Finished, null), null)
  }

  // Store path information
  private def storePathInfo(result: (TransitionResult, RecordedTransition),
                            successor: (ModelInstance, Transition),
                            backtracked: Boolean,
                            failed: Boolean): Unit = {

    val model = successor._1
    val trans = successor._2

    // record choices in the current transition
    if (result._2.recordedChoices.nonEmpty)
      trans.recordedChoices = result._2.recordedChoices

    // Store path information including the model name,
    // model ID, executed transition and transition quality for path coverage,
    // if the configuration of path coverage is true. -Rui
    if (mbt.config.dotifyPathCoverage) {
      if (backtracked) { // backtracked case
        // Record next state into current transition,
        // when backtracked, the next state is the origin state

        val nextStateNextIf =
          trans.getNextStateNextIf(result._2.transition.origin, false)

        pathInfoRecorder += new PathInfo(model.className,
                                         model.mIdx,
                                         trans,
                                         nextStateNextIf,
                                         TransitionQuality.backtrack)

      } else if (failed) { // failed case
        val nextStateNextIf =
          trans.getNextStateNextIf(result._2.transition.dest, false)
        pathInfoRecorder += new PathInfo(model.className,
                                         model.mIdx,
                                         trans,
                                         nextStateNextIf,
                                         TransitionQuality.fail)
        // add this failed transition to trie
        if (mbt.config.dotifyPathCoverage) trie.insert(pathInfoRecorder)
        //return result
      } else { // success case
        // Record next state into current transition.
        // next state is NOT null when result of "nextIf" condition is true,
        // record this next state, otherwise,
        // record the current transition's dest as the next state
        if (result._2.nextState != null) {
          val nextStateNextIf =
            trans.getNextStateNextIf(result._2.nextState.dest, true)

          pathInfoRecorder += new PathInfo(model.className,
                                           model.mIdx,
                                           trans,
                                           nextStateNextIf,
                                           TransitionQuality.OK)
        } else {
          val nextStateNextIf =
            trans.getNextStateNextIf(result._2.transition.dest, false)

          pathInfoRecorder += new PathInfo(model.className,
                                           model.mIdx,
                                           trans,
                                           nextStateNextIf,
                                           TransitionQuality.OK)
        }
      }
    }
  }

  private def insertPathInfoInTrie: Unit = {
    // output all executed transitions of the current test - Rui
    for (p <- pathInfoRecorder)
      Log.debug(
        "Recorded information for path coverage: " + p.toString + ", transID:" + p.transition.idx + ", nextif:" + p.nextStateNextIf)
    // Put information in pathInfoRecoder to the trie by
    // inserting all the information of the current test into a trie for path coverage,
    // if the configuration of path coverage is true. - Rui
    if (mbt.config.dotifyPathCoverage) trie.insert(pathInfoRecorder)
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
      assert(!trans.isSynthetic)
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

  def ppTrans(recTrans: RecordedTransition): String = {
    val transStr =
      Modbat.ppTrans(mbt.launchedModels.size,
                     recTrans.trans.ppTrans(true),
                     recTrans.transition,
                     recTrans.recordedAction,
                     recTrans.model.name)
    if (mbt.config.showChoices && recTrans.randomTrace != null &&
        recTrans.randomTrace.length != 0) {
      val choices = recTrans.debugTrace.mkString(", ")
      transStr + "; choices = (" + choices + ")"
    } else {
      transStr
    }
  }

  def printTrace(transitions: List[RecordedTransition]): Unit = {
    Log.warn("Error found, model trace:")
    for (t <- transitions) {
      Log.warn(ppTrans(t))
      for (u <- t.updates) {
        Log.warn("  " + u._1 + " = " + u._2)
      }
    }
  }
}
