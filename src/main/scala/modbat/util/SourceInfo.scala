package modbat.util

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.reflect.Method
import java.net.URL
import java.util.jar.JarFile

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import scala.language.existentials

import modbat.log.Log
import modbat.dsl.Action

object SourceInfo {
  abstract class InternalAction

  class Launch(val launchedModel: String) extends InternalAction

  class Choice(val choices: List[String]) extends InternalAction

  val SKIP = "\u0000"
  val MAXLEN = 20
}

class SourceInfo(val classLoaderURLs: Array[URL]) {
  import SourceInfo.{InternalAction,Choice,Launch}
  import SourceInfo.{SKIP,MAXLEN}

  val cachedActionInfoFromClass = new HashMap[Class[_], String]
  val cachedActionInfoFromMethod = new HashMap[Method, String]
  val cachedSourceInfoFromClass = new HashMap[Class[_], String]
  val cachedSourceInfoFromMethod = new HashMap[Method, String]
  val cachedLaunchChoiceInfoFromClass =
    new HashMap[Class[_], List[InternalAction]]
  val cachedLaunchChoiceInfoFromMethod =
    new HashMap[Method, List[InternalAction]] // TODO

  class SourceRecord {
    var source: String = _
    var line: Int = _
  }

  class ClInfoRecord {
    var name: String = _
  }

  class ActionRecord {
    val closure = new ClInfoRecord
    var methodInfo: String = _
  }

  class LaunchesAndChoices {
    val closure = new ClInfoRecord
    val actions = new ListBuffer[InternalAction]
  }

  class LineNumberVisitor(val r: SourceRecord)
    extends org.objectweb.asm.MethodVisitor(Opcodes.ASM4) {

    override def visitLineNumber(line: Int, start: org.objectweb.asm.Label) {
      r.line = line
    }
  }

  def analyzeClosure(visitor: ClassVisitor, closureName: String) {
    val cr = new ClassReader(findInURLs(closureName + ".class",
			     classLoaderURLs))
    try {
      cr.accept(visitor, 0)
    } catch {
      case e: ClassNotFoundException => {
	Log.warn(closureName.replace('.', File.separatorChar) +
		 ".class: file not found")
      }
    }
  }

  class SourceInfoMethodVisitor(val r: SourceRecord, m: Method)
    extends ClassVisitor(Opcodes.ASM4) {
    val mName = m.getName
    val mDesc = Type.getMethodDescriptor(m)

    override def visitSource(source: String, debug: String) {
      r.source = source
    }

    override def visitMethod(access: Int, name: String, desc: String,
			     signature: String, exceptions: Array[String]) = {
      if (name.equals(mName) && desc.equals(mDesc)) {
	new LineNumberVisitor(r)
      } else {
	null
      }
    }
  }

  class SourceInfoClsVisitor(val r: SourceRecord)
    extends ClassVisitor(Opcodes.ASM4) {

    override def visitSource(source: String, debug: String) {
      r.source = source
    }

    override def visitMethod(access: Int, name: String, desc: String,
			     signature: String, exceptions: Array[String]) = {
      if (name.equals("<init>")) { // constructor
	new LineNumberVisitor(r)
      } else {
	null
      }
    }
  }

  /* Write last method name to r.method.
     It is currently not clear which methods before the last one
     should be chosen, as many field accesses, and boxed items,
     have calls to methods that do not appear in the source code.
     TODO: In the future, called methods should probably be filtered
     against not being synthetic. */
  class MethodActionVisitor(val r: ActionRecord)
    extends org.objectweb.asm.MethodVisitor(Opcodes.ASM4) {

    override def visitMethodInsn(opcode: Int, owner: String,
				 name: String, desc: String) {
      if (owner.startsWith("modbat/dsl/Predef")) {
	if (name.equals("skip")) {
	  r.methodInfo = SKIP // empty string would result in fall-back label
	}
	if (!name.equals("assert")) {
	  return
	}
      }
      if (name.contains("$$$")) {
	return
      }
      if (name.contains('$')) {
	if (r.methodInfo == null) {
	  val to = name.lastIndexOf('$')
	  val eq = name.indexOf("_$eq")
	  if (eq != -1) {
	    r.methodInfo = name.substring(0, eq) + " = ..."
	    return
	  }
	  if (to == -1) {
	    r.methodInfo = name
	  } else {
	    val from = name.lastIndexOf('$', to - 1) + 1
	    r.methodInfo = name.substring(from, to)
	  }
	}
      } else {
	if (!name.contains("init>")) {
	  r.methodInfo = name
	} else { // TODO: clinit should be rendered a bit differently
	  if (owner.contains('$')) {
	    r.closure.name = owner
	  } else {
	    val slash = owner.lastIndexOf('/')
	    r.methodInfo = "new " + owner.substring(slash + 1)
	  }
	}
      }
    }
  }

  class MethodClosureVisitor(val r: ClInfoRecord)
    extends org.objectweb.asm.MethodVisitor(Opcodes.ASM4) {

    override def visitMethodInsn(opcode: Int, owner: String,
				 name: String, desc: String) {
      if (!name.contains("$$anonfun$apply$") &&
	  desc.contains("$$anonfun$")) {
	r.name = owner
      }
    }
  }

  class ActionInfoClsVisitor(val r: ActionRecord)
    extends ClassVisitor(Opcodes.ASM4) {
    val analyzed = (r.closure.name != null)

    override def visitMethod(access: Int, name: String, desc: String,
			     signature: String, exceptions: Array[String]) = {
      if (name.equals("apply") &&
	  !desc.equals("()V") &&
	  !desc.equals("()Ljava/lang/Object;")) {
	new MethodActionVisitor(r)
      } else if (name.startsWith("apply$mc") &&
	  name.endsWith("$sp")) {
	new MethodActionVisitor(r)
      } else if (name.equals("apply")) {
	new MethodClosureVisitor(r.closure)
      } else {
	null
      }
    }

    /* Functions hidden in closures (such as "maybe") are not
     * compiled in this way; instead of apply$mc...$sp methods, only
     * apply methods are generated. These in turn call an apply method
     * of a different class that finally contains the desired apply$mc
     * method. */
    override def visitEnd() {
      if (r.methodInfo == null) {
	r.methodInfo = ""
	if (!analyzed && r.closure.name != null) {
	  analyzeClosure(new ActionInfoClsVisitor(r), r.closure.name)
	  Log.debug("Function \"" + r.methodInfo +
		    "\" called inside closure (such as \"maybe\").")
	}
      }
      if (r.methodInfo.length > MAXLEN) {
	r.methodInfo = r.methodInfo.substring(0, MAXLEN - 3) + "..."
      }
    }
  }

  class LaunchAndChoiceVisitor(val r: LaunchesAndChoices)
    extends ClassVisitor(Opcodes.ASM4) {

    override def visitMethod(access: Int, name: String, desc: String,
			     signature: String, exceptions: Array[String]) = {
      if (name.startsWith("apply$mc") &&
	  name.endsWith("$sp")) {
	new MethodLaunchChoiceVisitor(r)
      } else if (name.equals("apply")) {
	//new MethodLaunchChoiceVisitor(r)
	new MethodClosureVisitor(r.closure)
      } else {
	null
      }
    }
  }

  class MethodLaunchChoiceVisitor(val r: LaunchesAndChoices)
    extends org.objectweb.asm.MethodVisitor(Opcodes.ASM4) {
    val choices = new ListBuffer[String]
    var launchedModel: String = null

    def closureNames(choices: List[String]) = {
      val closures = new ListBuffer[String]
      // TODO: If multiple closures result in the same string, add line number
      choices.foreach {
	ch => {
	  val ar = new ActionRecord
	  ar.closure.name = ch
	  analyzeClosure(new ActionInfoClsVisitor(ar), ch)
	  val clAction = ar.methodInfo
	  val cls = Class.forName(ch.replace(File.separatorChar, '.'))
	  if (clAction.endsWith("= ...")) {
	    closures +=
	      (clAction + ": line " + computeSourceInfo(cls, null, true))
	  } else {
	    closures += clAction
	  }
	}
      }
      closures.toList
    }

    def simpleClassName(fullClassName: String) = {
      val idx = fullClassName.lastIndexOf("/")
      if (idx == -1) {
	fullClassName // no package
      } else {
	fullClassName.substring(idx + 1)
      }
    }

    override def visitMethodInsn(opcode: Int, owner: String,
				 name: String, desc: String) {
      name match {
	case "<init>" => {
          // ensure these are calls to scala.runtime.AbstractFunction0
	  // if so, add closure to list of closures
	  if (owner.contains("$$anonfun$") && owner.contains("$apply$")) {
	    choices += owner
	  } else {
	    choices.clear
	    launchedModel = owner
	  }
	}
	case "wrapRefArray" => {
	  if (!owner.startsWith("scala/Predef")) {
	    choices.clear
	  }
	}
	case "choose" => {
	  // if choice is on a sequence, we assume it is a sequence of
	  // closures (as other sequences are not supported at this time)
	  if (owner.startsWith("modbat/dsl/Predef") &&
	      desc.equals("(Lscala/collection/Seq;)Ljava/lang/Object;")) {
	    val closures = closureNames(choices.toList)
	    r.actions += new Choice(closures)
	  } else {
	    choices.clear
	  }
	}
	case "launch" => {
	  if (owner.startsWith("modbat/mbt/Predef") &&
	      desc.equals("(Lmodbat/mbt/Model;)Lmodbat/mbt/ModelInstance;")) {
	    if (launchedModel != null) {
	      // should be non-null but this is not the case if
	      // the code to launch the model was never executed
	      r.actions += new Launch(simpleClassName(launchedModel))
	    }
	  }
	}
	case "transfuncToAction" => {
	  if (!owner.startsWith("modbat/dsl/Predef")) {
	    choices.clear
	  }
	}
	case "maybe" => {
	  if (owner.startsWith("modbat/dsl/Predef")) {
	    assert (choices.size == 1)
//	    r.closure.name = choices.head // TODO: Store closure info for later
	  }
	  choices.clear
	}
	case _ => {
	  choices.clear
	  launchedModel = null
	}
      }
    }
  }

  def jarEntry (filename: String, jar: JarFile) = {
    val entry = jar.getEntry(filename)
    if (entry != null) {
      jar.getInputStream(entry)
    } else {
      null
    }
  }

  def findPath(cls: Class[_ <: Any]): InputStream = {
    val filename = cls.getName.replace('.', '/') + ".class"
    return findInURLs(filename, classLoaderURLs)
  }

  def findInURLs(filename: String, urls: Array[URL]): InputStream = {
    for (url <- urls) {
      val basename = url.getFile()
      if (!basename.isEmpty()) {
	val file = new File(basename)
	if (file.exists()) {
	  if (basename.endsWith(".jar")) {
	    // use forward slashes for jar file entry
	    val probedJarEntry = jarEntry(filename, new JarFile(file))
	    if (probedJarEntry != null) {
	      return probedJarEntry
	    }
	  } else {
	    // use File.separatorChar for file
	    val filename2 = filename.replace('/', File.separatorChar)
	    val f = new File(basename + File.separatorChar + filename2)
	    if (f.exists()) {
	      return new FileInputStream(f)
            }
	  }
	  Log.debug(filename + " not found in " + basename + ".")
	} else {
	  Log.warn("Warning: class path entry " + basename + " not found.")
	}
      } else {
	Log.info("Skipping non-file URL " + url + ".")
      }
    }
    Log.error("Class file " + filename + " cannot be loaded.")
    throw new ClassNotFoundException()
  }

  def pkgNameAsDir(cls: Class[_ <: Any]) = {
    val pkg = cls.getPackage
    if (pkg == null) {
      "."
    } else {
      pkg.getName.replace('.', File.separatorChar)
    }
  }

  def clsNotFoundMsg(cls: Class[_]) {
    Log.error(cls.getName.replace('.', File.separatorChar) + ".class" +
	      ": file not found")
  }

  def computeActionInfo(action: Action, cls: Class[_], method: Method,
			includeLine: Boolean) = {
    val r = new ActionRecord
    try {
      val cr = new ClassReader(findPath(cls))
      if (method == null) {
	val cv = new ActionInfoClsVisitor(r)
	cr.accept(cv, 0)
      } else {
	r.methodInfo = method.getName
      }
      if (includeLine) {
	r.methodInfo + ": line " + sourceInfo(action, true)
      } else {
	r.methodInfo
      }
    } catch {
      case e: ClassNotFoundException => {
	clsNotFoundMsg(cls)
	""
      }
    }
  }

  // return info on which methods are called inside f
  def actionInfo(action: Action, includeLine: Boolean): String = {
    assert (action.transfunc != null)
    /* TODO: Test against skip
      return "" */
    val cls = getClass(action)
    val method = action.method
    if (method == null) {
      cachedActionInfoFromClass.getOrElseUpdate(cls,
	computeActionInfo(action, cls, null, includeLine))
    } else {
      cachedActionInfoFromMethod.getOrElseUpdate(method,
        computeActionInfo(action, cls, method, includeLine))
    }
  }

  // return info on launch/choose commands inside action
  def launchAndChoiceInfo(action: Action): List[InternalAction] = {
    if (action.transfunc == null) {
      return Nil
    }
    val cls = getClass(action)
    val method = action.method
    if (method == null) {
      cachedLaunchChoiceInfoFromClass.getOrElseUpdate(cls,
	computeLaunchChoiceInfo(cls, null))
    } else {
      cachedLaunchChoiceInfoFromMethod.getOrElseUpdate(method,
        computeLaunchChoiceInfo(cls, method)) // TODO
    }
  }

  def computeLaunchChoiceInfo(cls: Class[_], method: Method) = {
    val r = new LaunchesAndChoices
    try {
      val cr = new ClassReader(findPath(cls))
      if (method == null) {
	val cv = new LaunchAndChoiceVisitor(r)
	cr.accept(cv, 0)
//      } else { // TODO
//	val cv = new ActionInfoMethodVisitor(r, method)
//	cr.accept(cv, 0)
      }
      r.actions.toList
    } catch {
      case e: ClassNotFoundException => {
	clsNotFoundMsg(cls)
	Nil
      }
    }
  }

  def getClass(action: Action) = {
    val method = action.method
    if (method != null) {
      method.getDeclaringClass
    } else {
      action.transfunc.getClass
    }
  }

  def sourceInfo(action: Action, lineOnly: Boolean): String = {
    if (action.transfunc == null) {
      return "(empty transition function)"
    }
    val cls = getClass(action)
    val method = action.method
    if (method == null) {
      cachedSourceInfoFromClass.getOrElseUpdate(cls,
	computeSourceInfo(cls, null, lineOnly))
    } else {
      cachedSourceInfoFromMethod.getOrElseUpdate(method,
        computeSourceInfo(cls, method, lineOnly))
    }
  }

  def computeSourceInfo(cls: Class[_], method: Method, lineOnly: Boolean) = {
    val pkgName = pkgNameAsDir(cls)
    val r = new SourceRecord()
    try {
      val cr = new ClassReader(findPath(cls))
      if (method == null) {
	val cv = new SourceInfoClsVisitor(r)
	cr.accept(cv, 0)
      } else {
	val cv = new SourceInfoMethodVisitor(r, method)
	cr.accept(cv, 0)
      }
      if (lineOnly) {
	r.line.toString
      } else {
	pkgName + File.separatorChar + r.source + ":" + r.line
      }
    } catch {
      case e: ClassNotFoundException => {
	clsNotFoundMsg(cls)
	""
      }
    }
  }
}
