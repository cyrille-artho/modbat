package modbat.mbt

import java.io.File
import java.lang.annotation.Annotation
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.URL
import java.net.URLClassLoader

import scala.collection.Iterator
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex
import modbat.RequirementFailedException
import modbat.dsl.Action
import modbat.dsl.After
import modbat.dsl.Before
import modbat.dsl.Model
import modbat.dsl.NextStateOnException
import modbat.dsl.Observer
import modbat.dsl.State
import modbat.dsl.States
import modbat.dsl.Throws
import modbat.dsl.Trace
import modbat.dsl.Weight
import modbat.dsl.Transition
import modbat.log.Log
import modbat.trace._
import modbat.util.CloneableRandom
import modbat.util.Random

import com.miguno.akka.testing.VirtualTime

//import com.miguno.akka.testing.VirtualTime
/** Contains core functionality for loading and running model.
  * Model exploration code is in Modbat. */
object MBT {
  var isOffline = true
  var classLoaderURLs: Array[URL] = null

  def invokeMethod(m: Method, inst: Object): Unit = {
    try {
      m.invoke(inst)
    } catch {
      case invoc: InvocationTargetException => throw invoc.getCause
    }
  }

  def configClassLoader(classpath: String): Unit = {
    val sep = System.getProperty("path.separator")
    val paths = classpath.split(sep)
    val urls = ListBuffer[URL]()
    for (p <- paths) {
      Log.debug("Adding " + p + " to classpath.")
      urls += new File(p).toURI.toURL()
    }
    classLoaderURLs = urls.toArray
  }

  def printStackTrace(trace: Array[StackTraceElement]): Unit = {
    for (el <- trace) {
      val clsName = el.getClassName
      if (clsName.startsWith("modbat.mbt.") &&
          !clsName.startsWith("modbat.mbt.Predef")) {
        return
      }
      Log.error("\tat " + el.toString)
    }
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
}

class MBT (val config: Configuration) {
  var modelClass: Class[_ <: Any] = null // main model class
  val launchedModels = new ArrayBuffer[ModelInstance]()
  val launchedModelInst = new ArrayBuffer[Model]()
  val invokedStaticMethods = new HashSet[Method]()
  val transitionQueue = new Queue[(ModelInstance, String)]()
  val firstInstance = new LinkedHashMap[String, ModelInstance]()
  var rng: Random = null
  /***var rethrowExceptions = false // for offline testing (to be done)*/
  var or_else = false // true if or_else predicate has just been evaluated
  // used to skip subsequent "maybe", which is called just afterwards
  // (as sequential maybe... or_else is interpreted as nested operators)
  var checkDuplicates = false
  // true if model is first loaded in model exploration mode (but not
  // offline mode) to give a warning about duplicate labels
  var testHasFailed = false
  var externalException: Throwable = null // for failures in other threads
  val modbatThread = Thread.currentThread
  val warningIssuedOn = new HashSet[Object]()
  // do not issue same warning twice for static model problem
  var currentTransition: Transition = null
  val stayLock = new AnyRef()
  val time = new VirtualTime

  // TODO: If necessary, add another argument (tag) to distinguish between
  // different types of warnings for the same type of object/data.
  def warningIssued(o: Object) = {
    if (!warningIssuedOn(o)) {
      warningIssuedOn += o
      false
    } else {
      true
    }
  }

  // return methods of class or master model class if no instance given
  def getMethods(instance: Model) = {
    if (instance == null) {
      modelClass.getMethods()
    } else {
      instance.getClass.getMethods()
    }
  }

  def invokeAnnotatedStaticMethods(annotationType: Class[_ <: Annotation],
                                   instance: Model): Unit = {
    invokeAll(annotationType, instance, handleStatic)
  }

  def invokeAnnotatedMethods(annotationType: Class[_ <: Annotation],
                             instance: Model): Unit = {
    invokeAll(annotationType, instance, handleDynamic)
  }

  def invokeAll(annotationType: Class[_ <: Annotation],
                model: Model,
                handler: (Class[_ <: Annotation], Method, Object) => Unit): Unit = {
    val methods = getMethods(model)
    for (m <- methods) {
      val annotation = m.getAnnotation(annotationType)
      if (annotation != null) {
        handler(annotationType, m, model)
      }
    }
  }

  def handleStatic(annotationType: Class[_ <: Annotation],
                   m: Method,
                   instance: Object): Unit = {
    if ((m.getModifiers() & Modifier.STATIC) != 0) {
      if (!invokedStaticMethods.contains(m)) {
        invokedStaticMethods += m
        Log.debug(annotationType.getSimpleName() + ": static " + m.getName())
        m.invoke(instance)
      }
    }
  }

  def handleDynamic(annotationType: Class[_ <: Annotation],
                    m: Method,
                    instance: Object): Unit = {
    if ((m.getModifiers() & Modifier.STATIC) == 0) {
      Log.debug(annotationType.getSimpleName() + ": " + m.getName())
      m.invoke(instance)
    }
  }

  def setRNG(r: Random): Unit = {
    rng = r
  }

  def setRNG(seed: Long): Unit = {
    rng = new CloneableRandom(seed)
  }

  def setTestFailed(failed: Boolean): Unit = {
    testHasFailed = failed
  }

  def testFailed() = testHasFailed

  def getRandomSeed() = rng.getRandomSeed

  def clearLaunchedModels(): Unit = {
    launchedModels.clear()
    launchedModelInst.clear()
    transitionQueue.clear()
    or_else = false
    testHasFailed = false
    currentTransition = null
  }

  def getLaunchedModel(i: Int) = launchedModels(i)

  def loadModelClass(className: String): Unit = {
    /* load model class */
    try {
      val classloader =
        new URLClassLoader(MBT.classLoaderURLs,
                           Thread.currentThread().getContextClassLoader())
      modelClass = classloader.loadClass(className)
    } catch {
      case e: ClassNotFoundException => {
        Log.error("Class \"" + className + "\" not found.")
        throw e
      }
    }
  }

  def prepare(instance: Model): Unit = {
    if (config.setup) {
      // Avoid invoking companion object methods on launched instances
      // Solution: Avoid calling static methods more than once.
      invokeAnnotatedStaticMethods(classOf[Before], instance)
      invokeAnnotatedMethods(classOf[Before], instance)
    }
  }

  def cleanup(): Unit = {
    // clear buffer of static methods for itself
    invokedStaticMethods.clear()
    if (config.cleanup) {
      val instances = launchedModelInst.reverse
      instances.foreach(inst => invokeAnnotatedMethods(classOf[After], inst))
      instances.foreach(inst =>
        invokeAnnotatedStaticMethods(classOf[After], inst))
      invokedStaticMethods.clear()
      // clear buffer of static methods again for next prepare, if needed
    }
  }

  def findConstructor(c: Class[Model]) = {
    try {
      c.getConstructor()
    } catch {
      case e: NoSuchMethodException => {
        Log.error("No suitable constructor found.")
        Log.error(
          "A public nullary constructor is needed " +
            "to instantiate the primary model.")
        Log.error("Consider adding a constructor variant:")
        Log.error("  def this() = this(...)")
        throw (e)
        null
      }
    }
  }

  def mkModel(modelInstance: Model) = {
    try {
      if (modelInstance != null) {
        modelInstance
      } else {
        assert(Transition.pendingTransitions.isEmpty)
        val cons = findConstructor(modelClass.asInstanceOf[Class[Model]])
        cons.newInstance().asInstanceOf[Model]
      }
    } catch {
      case c: ClassCastException => {
        Log.error("Model class does not extend Model.")
        Log.error("Check if the right class was specified.")
        throw (c)
        null
      }
      case e: InstantiationException => {
        Log.error("Cannot instantiate model class.")
        Log.error("The class must not be abstract or an interface.")
        throw (e)
        null
      }
      case e: InvocationTargetException => {
        Log.error("Exception in default (nullary) constructor of main model.")
        Log.error("In dot mode, a constructor that ignores any data")
        Log.error("is sufficient to visualize the ESFM graph.")
        if (!config.printStackTrace) {
          Log.error("Use --print-stack-trace to see the stack trace.")
        } else {
          val cause = e.getCause
          Log.error(cause.toString)
          MBT.printStackTrace(cause.getStackTrace)
        }
        throw (e)
        null
      }
    }
  }

  // Either called from within a model transition, with a given
  // model instance, or from runTests, where a model instance
  // and the transition system are created using reflection
  // if modelInstance == null: initial model
  def launch(modelInstance: Model): ModelInstance = {
    val model = mkModel(modelInstance)

    if (Transition.pendingTransitions.isEmpty) {
      Log.error("Model " + model.getClass.getName + " has no transitions.")
      Log.error("Make sure at least one transition exists of type")
      Log.error("  \"a\" -> \"b\" := { code } // or, for an empty transition:")
      Log.error("  \"a\" -> \"b\" := skip")
      throw new NoTransitionsException(model.getClass.getName)
    }
    val inst = new ModelInstance(this, model, Transition.getTransitions)
    Transition.clear
    inst.addAndLaunch(modelInstance == null)
  }

  // choose between min (inclusive) and max (exclusive)
  def choose(min: Int, max: Int) = {
    if (min == max) {
      // size of range is 0 but return min to avoid division by 0
      min
    } else {
      rng.choose(min, max)
    }
  }

  def maybe(action: Action) = {
    if (or_else) {
      or_else = false
      action.transfunc()
    } else {
      // compute choice for "maybe" -Rui
      val choice
        : Boolean = rng.nextFloat(true) < config.maybeProbability
      // record choice for "maybe" -Rui
      val maybeChoice = MaybeChoice(choice)
      rng.recordChoice(maybeChoice)
      if (choice) {
        action.transfunc()
      }
    }
  }
  // all maybeBool things need to be deleted -Rui
  def maybeBool(pred: () => Boolean) = {
    if (rng.nextFloat(true) < config.maybeProbability) {
      pred()
    } else {
      false
    }
  }
}
