package modbat.mbt

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex

import modbat.RequirementFailedException
import modbat.dsl.Action
import modbat.dsl.Model
import modbat.dsl.NextStateOnException
import modbat.dsl.Observer
import modbat.dsl.State
import modbat.cov.StateCoverage
import modbat.dsl.States
import modbat.dsl.Trace
import modbat.dsl.Throws
import modbat.dsl.Transition
import modbat.cov.{StateCoverage, TransitionAverageReward, TransitionCoverage}
import modbat.trace.Backtrack
import modbat.trace.ExceptionOccurred
import modbat.trace.ExpectedExceptionMissing
import modbat.trace.TracedFields
import modbat.trace.RecordedTransition
import modbat.trace.TransitionResult
import modbat.util.SourceInfo
import modbat.dsl.Weight
import modbat.log.Log
import modbat.mbt.{MBT => mbt}

class ModelInstance (/*val mbt: MBT, */val model: Model, val trans: List[Transition]) {
  val className = model.getClass.getName
  var states = HashMap[String, State]()
  val transitions = new ListBuffer[Transition]()
  var initialState: State = null
  var currentState: State = null
  var name = setModelName
  /* number of nextIf transition taken, -1 if none taken */
  var expectedOverrideTrans = -1
  var expectedException: String = null
  var isObserver = false
  var joining: ModelInstance = null
  val tracedFields = new TracedFields(getTracedFields, model)
  @volatile var staying = false
  val mIdx = MBT.launchedModels.count(_.className.equals(className)) // mIdx gives the ID of the model -Rui

  /* isChild is true when coverage information of initial instance is
   * to be re-used; this is the case when a child is launched, but also
   * when a model instance is created again after the first test run. */
  def init(isChild: Boolean) {
    for (tr <- trans) {
      regTrans(tr, isChild)
    }
  }

  def getCurrentState = currentState.name

  def getModel() = model

  def getTracedFields = {
    var cls: Class[_] = model.getClass
    val fields = getTracedFieldsInClass(cls)
    cls = cls.getSuperclass
    while (!cls.getName.equals("java.lang.Object")) {
      fields ++= getTracedFieldsInClass(cls)
      cls = cls.getSuperclass
    }
    fields.toList
  }

  def getTracedFieldsInCons(c: Constructor[_]) = {
    var i = 0
    val paramAnns = c.getParameterAnnotations()
    while (i < paramAnns.length) {
      val p = paramAnns(i)
      for (ann <- p) {
         if (ann.isInstanceOf[Trace]) {
           Log.debug("Trace param. " + i)
         }
      }
      i += 1
    }
  }

  def getTracedFieldsInClass(cls: Class[_]) = {
    val fields = new ListBuffer[Field]
    for (c <- cls.getConstructors) {
      /*fields ++= */getTracedFieldsInCons(c)
      // TODO: Java 8 does not seem to report field names correctly
      // wait for fix or use bytecode library
    }
    for (f <- cls.getDeclaredFields()) {
      if (f.getAnnotation(classOf[Trace]) != null) {
         Log.debug("Trace field " + cls + "." + f.getName)
         fields += f
      }
    }
    fields
  }

  def warnAboutNonDefaultWeights: Unit = {
    for (t <- transitions filter (_.action.weight != 1.0)) {
      if (!mbt.warningIssued((className, t.coverage))) {
         Log.warn("Observer " + className + ": transition " + t +
         	 " has non-default weight, which is ignored.")
      }
    }
  }

  def addAndLaunch(firstLaunch: Boolean) = {
    if (/***mbt.m*/Modbat.firstInstance.contains(className)) {
      val master =
         initChildInstance(className, trans.toArray)
      regSynthTrans(true)
      registerStateSelfTrans(model, true)
      TransitionCoverage.reuseCoverageInfo(this, master, className)
    } else {
      /***mbt.m*/Modbat.firstInstance.put(className, this)
      init (false)
      regSynthTrans(false)
      registerStateSelfTrans(model, false)
    }
    model.efsm = this
    model.mbt = /***mbt*/new MBT()
    mbt.prepare(model)
    Log.fine("Launching new model instance " + name + "...")
    if (model.isInstanceOf[Observer]) {
      isObserver = true
        if (firstLaunch) {
                  Log.error("Primary model must not be of type Observer.")
           throw new FirstLaunchObserverException(name)
      }
      warnAboutNonDefaultWeights
    }
    mbt.launchedModels += this
    mbt.launchedModelInst += model
    currentState = initialState
    StateCoverage.cover(initialState)
    this
  }

  def join(modelInstance: Model): Unit = {
    if (modelInstance == null) {
      Log.error(name + " calls join(null) but parameter must be non-null.")
      throw new NullPointerException()
    }
    if (modelInstance.efsm == null) {
      Log.error(name + " calls join on model of type " +
         	modelInstance.getClass + ", which has not been launched yet.")
      Transition.pendingTransitions.clear // clear init'd but unlaunched model
      throw new modbat.dsl.JoinWithoutLaunchException()
    }
    assert(joining == null)
    joining = modelInstance.efsm
  }

  def initChildInstance(className: String, trans: Array[Any]) = {
    val master = /***mbt.m*/Modbat.firstInstance(className)
    Log.debug("Identical model found: " + master.name)
    states = master.states
    initialState = master.initialState
    init (true)
    master
  }

  def regSynthTrans(isChild: Boolean) {
    for (tr <- transitions) {
      for (t <- tr.nonDetExceptions) {
         assert (t.target.isSynthetic)
         regTrans(t.target, isChild)
      }
      for (t <- tr.nextStatePredicates) {
         assert (t.target.isSynthetic)
         regTrans(t.target, isChild)
      }
    }
  }

  def registerStateSelfTrans(model: Model, isChild: Boolean): Unit = {
    val methods = mbt.getMethods(model).sortBy(_.toGenericString)
    for (m <- methods) {
      val annotation = m.getAnnotation(classOf[States])
      if (annotation != null) {
         registerTrans(model, m, annotation, isChild)
      }
    }
  }

  def registerTrans(model: Model,
                    m: Method,
                    annotation: States,
                    isChild: Boolean): Unit = {
    val params = m.getParameterTypes
    if (params.length != 0) {
      if (!mbt.warningIssued(m)) {
        Log.warn(
          "Ignoring method " + m +
            ", which has @States annotation but more than zero arguments.")
      }
    } else {
      registerTransForStates(model, m, annotation, isChild)
    }
  }

  def getWeight(w: Weight, n: Int) = {
    if (n == 0) {
      0.0
    } else {
      if (w == null) {
         1.0 / n
      } else {
         w.value()
      }
    }
  }

  def registerTransForStates(model: Model,
                             m: Method,
                             annotation: States,
                             isChild: Boolean): Unit = {
    assert(Transition.pendingTransitions.isEmpty)
    val transStates = annotation.value()
    val n = (transStates filter(t => states.contains(t))).size
    val weight = getWeight(m.getAnnotation(classOf[Weight]), n)
    val exceptions = m.getAnnotation(classOf[Throws])
    for (state <- transStates) {
      if (states.contains(state)) {
        Log.debug(
          "Registering \"" + state + "\" -> \"" + state + "\" := "
            + m.getName)
         val st = states(state)
        val wrapper = { () => mbt.invokeMethod(m, model) }
         val action = new Action(model, wrapper, m)
         action.weight = weight
         if (exceptions != null) {
           action.throws(exceptions.value())
         }
         val lineNumber = SourceInfo.lineNumberFromMethod(m)
         val t =
           new Transition(model, st, st, false, action,
                          m.getDeclaringClass().getName(), lineNumber, false)
         regTrans(t, isChild, true)
      } else {
         if (!mbt.warningIssued((state, m))) {
           Log.warn("Ignoring non-existent state " + state +
         	   " from @States annotation for " + m.getName + ".")
         }
      }
    }
    assert(Transition.pendingTransitions.isEmpty)
  }

  /* Iterate through all existing instances to find highest ID.
   * If we ever have large models where this becomes too slow,
   * add a hash map mapping model class names to a counter */
  def setModelName: String = {
    var id: Int = 1
    for (m <- mbt.launchedModels) {
      if (m.className.equals(className)) {
         val n = Integer.parseInt(m.name.substring(m.name.indexOf('-') + 1))
         if (n >= id) {
           id = n + 1
         }
      }
    }
    className + "-" + id
  }

  def uniqueState (state: State) = {
    val name = state.name
    if (states.contains(name)) {
      states(name)
    } else {
      states += (name -> state)
      setCoverageInfo(state)
      state
    }
  }

  def setCoverageInfo(s: State) {
    s.coverage = new StateCoverage
  }

  def regTrans(tr: Transition, isChild: Boolean,
                ignoreDuplicates: Boolean = false) {
    tr.origin = uniqueState(tr.origin)
    tr.dest = uniqueState(tr.dest)
    Log.debug(
      name + ": Registered state transition from " + tr.origin +
        " to " + tr.dest)
    if (mbt.checkDuplicates && !ignoreDuplicates) {
      val label = tr.action.label
      if (!label.isEmpty) {
         val duplicates =
           transitions filter (t => t.action.label.equals(label))
         if (duplicates.length != 0) {
           if (!mbt.warningIssued(label)) {
             Log.warn ("Duplicate transition label \"" + label + "\".")
           }
         }
      }
    }
    // set the transition ID for the new transition -RUI
    tr.idx = transitions.size
    transitions += tr

    if (isChild) {
      return
    }

    tr.coverage = new TransitionCoverage(/***/Main.config)
    tr.averageReward = new TransitionAverageReward(/***/Main.config) // averageReward of the transition - Rui
    if (tr.isSynthetic) {
      return
    }

    if (initialState == null) {
      initialState = tr.origin
    }
    val matchingTrans =
      transitions filter (t =>
        !t.isSynthetic &&
          (t.origin.name.equals(tr.origin.name)) &&
          (t.dest.name.equals(tr.dest.name)))

    if (matchingTrans.length > 1) {
      if (matchingTrans.length == 2) {
         // Second matching transition "discovered", assign number to both
         matchingTrans(0).n = 1
         matchingTrans(1).n = 2
      } else {
         assert (matchingTrans.length > 2)
        tr.n = matchingTrans.length
         // no need to re-assign number to all previous matching transitions
      }
    }
  }


  /* From stack trace with assertion failure, find model function */
  def findModelFunction(trace: Array[StackTraceElement]): StackTraceElement = {
    var prev: StackTraceElement = null
    for (el <- trace) {
      val clsName = el.getClassName
      List("modbat.mbt", "sun.reflect", "java.lang.reflect").foreach {
         m => {
           if (clsName.startsWith(m) &&
               !prev.getClassName.startsWith("scala.Predef") &&
               !prev.getClassName.startsWith("modbat.mbt.Predef")) {
             return prev
           }
         }
      }
      prev = el
    }
    prev
  }

  def findMatchingFunction(trace: Array[StackTraceElement],
         		   function: () => Any): StackTraceElement = {
    val clsName = function.getClass.getName
    for (el <- trace) {
      if (el.getClassName().startsWith(clsName)) {
         return el
      }
    }
    null
  }

  def printStackTraceIfEnabled(e: Throwable) {
    if (/***mbt.enableStackTrace*/Main.config.printStackTrace) {
       Log.error(e.toString)
       mbt.printStackTrace(e.getStackTrace)
     }
  }

  def handle(e: Throwable, successor: Transition):
    (TransitionResult, RecordedTransition) = {
    if (!expected(successor.expectedExceptions, e)) {
      val excTrans = nonDetExc(successor.nonDetExceptions, e)
      if (excTrans eq null) {
         Log.warn(e + " occurred, aborting.")
         printStackTraceIfEnabled(e)
         val fName = successor.action.transfunc.getClass.getName
         if (fName.startsWith("modbat.mbt") ||
             fName.startsWith("scala.Predef")) {
           val func = findModelFunction(e.getStackTrace)
           return (ExceptionOccurred(e.toString),
                  new RecordedTransition(this,
                                         successor,
                                         func,
                                         null,
                                         e.getClass.getName))
         }
         val func =
           findMatchingFunction(e.getStackTrace, successor.action.transfunc)
         return (ExceptionOccurred(e.toString),
                new RecordedTransition(this,
                                       successor,
                                       func,
                                       null,
                                       e.getClass.getName))
      }
      Log.fine(e.toString() + " leads to exception state " +
               excTrans.dest.toString() + ".")
      Log.debug("Exceptional transition is at " + excTrans.sourceInfo)
      if (successor.action.immediate) {
         Log.fine("Next transition on this model must be taken immediately.")
      }
      return TransitionCoverage.cover(this,
                                      successor,
                                      excTrans,
                                      e.getClass.getName,
                                      successor.action.immediate)
    }
    Log.fine(e.toString() + " generated as expected.")
    return TransitionCoverage.cover(this, successor, null, e.getClass.getName)
  }

  def transNumToString(n: Int) = {
    if (n == 0) {
      ""
    } else {
      " #" + Integer.toString(n)
    }
  }

  /* check result of "nextIf", choose different successor state if
     condition holds */
  def checkNextStPred(trans: Transition):
    (TransitionResult, RecordedTransition) = {
    for (nextSt <- trans.nextStatePredicates) {
      if (!(nextSt.nonDet) ||
           (mbt.rng.nextFloat(true) < /***mbt.*/Main.config.maybeProbability)) {
         val envCallResult = nextSt.action() // result of "nextIf" condition
         Log.debug("Call to nextIf returns " + envCallResult + ".")
         // remember outcome of RNG if next state predicate should be checked
         // record outcome of nextSt.action() and check for consistency
         // during replay
         val transition = nextSt.target
         if (envCallResult) {
           if (!MBT.isOffline || (expectedOverrideTrans == transition.n)) {
             Log.fine(
               "Next state predicate" +
               transNumToString(transition.n) +
               " at " + nextSt.target.sourceInfo +
               " holds, go to state " +
               transition.dest + ".")
            expectedOverrideTrans = -1
            return TransitionCoverage.cover(this, trans, transition)
          }
        }
      }
    }
    TransitionCoverage.cover(this, trans)
  }

  def successors(quiet: Boolean): List[Transition] = {
    if (!quiet) {
      Log.debug (name + ": Current state: " + currentState)
    }
    val successorTrs =
       transitions.toList.filter (t => !t.isSynthetic &&
         			  t.origin == currentState)

    if (Log.isLogging(Log.Debug)) {
      for (trans <- successorTrs) {
         if (!quiet) {
           Log.debug(name + ": Possible successor: " + trans.dest)
         }
      }
    }
    successorTrs
  }

  def getTransition(i: Int) = transitions(i)

  def setExpectedException(excType: String): Unit = {
    expectedException = excType
  }

  def setExpectedOverrideTrans(t: Int): Unit = {
    expectedOverrideTrans = t
  }

  def handleReqFailure(req: Exception, successor: Transition):
    (TransitionResult, RecordedTransition) = {
      Log.fine("Precondition violated, backtracking to " +
                successor.origin + ".")
    (Backtrack, new RecordedTransition(this, successor)) // return the RecordedTransition when backtracking - Rui
  }

  /* returns result of transition function and next state (if available) */
  def executeTransition(successor: Transition):
    (TransitionResult, RecordedTransition) = {
    if (!MBT.isOffline) {
      Log.fine(name + ": Executing transition " + successor + "...")
    }
    if (successor.action.transfunc ne null) {
      try {
        mbt.currentTransition = successor
        TransitionCoverage.prep(successor)
        successor.action.transfunc()
        successor.action.stayTime match {
          case Some((t1, t2)) => {
            mbt.stayLock.synchronized {
              staying = true
            }
            val stayTime =
              (if (t1 == t2) t1 else mbt.rng.choose(t1, t2)).asInstanceOf[Long]
//            new Timer(stayTime).start()
            val wakeUp = new WakeUp()
            mbt.time.scheduler.scheduleOnce(stayTime.millis)(wakeUp.run)
//            mbt.time.scheduler.scheduleOnce(stayTime.millis)(new WakeUp())
          }
          case _ => ()
        }
         if (!successor.expectedExceptions.isEmpty) {
           Log.warn("Expected exception did not occur, aborting.")
           (ExpectedExceptionMissing, new RecordedTransition(this, successor))
         } else {
           checkNextStPred(successor)
         }
      } catch {
         case reqFailed: RequirementFailedException => {
           handleReqFailure(reqFailed, successor)
         }
         case illarg: IllegalArgumentException => {
           val msg = illarg.getMessage
           if (!/***mbt.*/Main.config.precondAsFailure && (msg != null) &&
               (msg.startsWith("requirement failed"))) {
             handleReqFailure(illarg, successor)
           } else { // treat precond. failure like normal exception
             handle(illarg, successor)
           }
         }
         case e: Throwable => handle(e, successor)
      }
    } else {
      Log.debug("Empty transition action.")
      checkNextStPred(successor)
    }
  }

/* check if any entry matches against exception,
   return successor state if so */
  def nonDetExc(excToStateMap: List[NextStateOnException],
                e: Throwable): Transition = {
    for (entry <- excToStateMap) {
      if (expected(List(entry.exception), e)) {
         return entry.target
      }
    }
    return null
  }

  def matchesType(ex: Throwable, excPattern: Regex): Boolean = {
    var e: Class[_] = ex.getClass
    while (e != null) {
      excPattern findFirstIn e.getName() match {
        case Some(e: String) => return true
        case _ =>
      }
      e = e.getSuperclass
    }
    false
  }

  /* check if exception matches against regex of expected exceptions */
  def expected(exc: List[Regex], e: Throwable): Boolean = {
    exc foreach (ex =>
      if (matchesType(e, ex)) {
        Log.debug("Expected: " + e)
        return true
      }
    )
    return false
  }

  def setWeight(label: String, weight: Double): Unit = {
    assert(weight >= 0)
    transitions.filter(_.action.label == label)
      .foreach(_.action.weight(weight))
  }

  def invokeTransition(label: String): Unit = {
    mbt.transitionQueue.enqueue((this, label))
  }

  class WakeUp() extends Thread {
//  class Timer(val t: Long) extends Thread {
    override def run() {
//      Log.fine(name + ": Started staying for " + t + " ms.")
//      Thread.sleep(t)
      mbt.stayLock.synchronized {
        staying = false
//        mbt.stayLock.notify()
      }
      Log.fine(name + ": Finished staying.")
    }
  }
}
