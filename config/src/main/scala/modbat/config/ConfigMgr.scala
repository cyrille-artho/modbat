package modbat.config

import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.math.BigInteger
import modbat.util.FieldUtil
import scala.math.Ordered
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

object ArgParse extends Enumeration {
  type ArgParse = Value
  val Ok, BareArg, Done, Exit = Value
  // BareArg = Store for later
  // Done = Done, app-specific part is pending, skip current arg "--"
  // Exit (--help) should exit
}

object ConfigMgr {
  import ArgParse._

  def main(args: Array[String]): Unit = {
    try {
        run(args)
    } catch {
      case e: Exception => System.exit(1)
    }
  }

  def printRemainingArgs(args: Option[Iterator[String]]): Unit = {
    args match {
      case Some(remainingArgs) => {
        // app-specific handling of left-over args; here: pretty-print
        val hasMore = remainingArgs.hasNext
        while (remainingArgs.hasNext) {
          print (remainingArgs.next())
            if (remainingArgs.hasNext) {
              print (" ")
            }
          }
          if (hasMore) {
            println()
          }
        }
      case None => // nothing
    }
  }

  def run(args: Array[String]): Unit = {
    // parse arguments
    var c: ConfigMgr = null
    try {
      c = new ConfigMgr("ConfigMgr", "[FILE]", new TestConfiguration(),
			new Version ("modbat.config"), true)
      printRemainingArgs(c.parseArgs(args))
      } catch {
        case e: IllegalArgumentException => {
	      if (c != null) {
	         Console.err.println(c.header)
	      }
	      Console.err.println(e.getMessage())
	      throw e
        }
      }
    }
  }

/** set @param test to <tt>true</tt> to enable assignments from values
    defined in <tt>@test</tt> annotations. */
class ConfigMgr (progName: String, argName: String,
		 config: Configuration,
		 version: Version, test: Boolean = false) {
  import ArgParse._
  import ConfigMgr._
  import FieldUtil._

  val NameLength = 23
  val ValueLength = 16
  val shorthands: HashMap[Char, String] = new HashMap()
  val bareArgs: ListBuffer[String] = new ListBuffer()
  val footnotes: ListBuffer[Array[String]] = new ListBuffer()
  var splashScreen: List[String] = null

  // build up table of shorthands and set test values for given fields
  val fields: Array[Field] = config.getClass().getDeclaredFields()
  for (f <- fields) {
    val shorthand = f.getAnnotation(classOf[Shorthand])
    if (shorthand != null) {
      shorthands += (shorthand.value() -> f.getName())
    }
    val envVar = fieldToEnvVar(f)
    val envVal = System.getenv(envVar)
    if (envVal != null) {
      val argName = "environment variable " + envVar
      if (f.getType().isPrimitive()) {
	parsePrimitiveType(f, argName, envVal)
      } else {
	parseString(f, argName, envVal)
      }
    }

    if (test) {
      val testAnnotation = f.getAnnotation(classOf[Test])
      if (testAnnotation != null) {
	if (isInt(f)) {
	  setInt(f, config, testAnnotation.intval())
	}
	if (isLong(f)) {
	  setLong(f, config, testAnnotation.longval())
	}
      }
    }
  }

  /** Die with error message. Need to return None to satisfy type check. */
  def die(msg: String): ArgParse = {
    throw new IllegalArgumentException(msg)
  }

  def HelpArgWidth = 32

  def docStr(f: Field) = {
    val docAnn = f.getAnnotation(classOf[Doc])
    if (docAnn != null) {
      docAnn.value()
    } else {
      ""
    }
  }

  def fieldToEnvVar(f: Field) = convertFieldName(f.getName(), '_', true)

  def fieldToCmdArg(f: Field) = {
    val name = f.getName()
    if (isBoolean(f)) {
      "[no-]" + convertFieldName(name, '-', false)
    } else {
      convertFieldName(name, '-', false)
    }
  }

  def convertFieldName(name: String, sep: Char, toUpper: Boolean) = {
    val chars = name.toCharArray()
    val cmdArg = new StringBuilder()
    for (ch <- chars) {
      if (Character.isUpperCase(ch)) {
	cmdArg.append(sep)
      }
      if (toUpper) {
	cmdArg.append(Character.toUpperCase(ch))
      } else {
	cmdArg.append(Character.toLowerCase(ch))
      }
    }
    cmdArg.toString()
  }

  def header = progName + " v" + version

  def showVersion = println(header)

  def usage: ArgParse = {
    showVersion
    println("Usage: " + progName + " [--OPTION=value] ... " + argName)
    println(formatStr("  -h, --help", HelpArgWidth) + "show this help and exit")
    println(formatStr("  -s, --show", HelpArgWidth) +
	    "show current configuration")
    println(formatStr("  -v, --version", HelpArgWidth) +
	    "show version number and exit")
    footnotes.clear()
    for (f <- fields) {
      val doc = new StringBuilder(docStr(f))
      val footnote = f.getAnnotation(classOf[Footnote])
      if (footnote != null) {
	footnotes += footnote.value()
	doc.append(" [" + footnotes.length + "]")
      }
      val shorthand = f.getAnnotation(classOf[Shorthand])
      if (shorthand != null) {
	println(formatStr("  -" + shorthand.value() +
			  ", --" + fieldToCmdArg(f) + " ",
			  HelpArgWidth) + doc)
      } else {
	println(formatStr("      --" + fieldToCmdArg(f) + " ",
			  HelpArgWidth) + doc)
      }
    }
    printFootnotes
  }

  def setSplashScreen(info: List[String]): Unit = {
    splashScreen = info
  }

  def showSplashScreen: Unit = {
    if (splashScreen != null) {
      splashScreen.foreach(println)
    }
  }

  def parseArgs(args: Array[String]): Option[Iterator[String]] = {
    showSplashScreen
    var finished: Boolean = false
    val argIt: Iterator[String] = args.iterator
    while (argIt.hasNext) {
      val current = argIt.next()
      val ap: ArgParse = parseArg(current)
      ap match {
	case BareArg => bareArgs += current
	case Done => return Some(bareArgs.iterator ++ argIt)
	case Exit => return None
	case Ok => // nothing
      }
    }
    val fields: Array[Field] = config.getClass().getDeclaredFields()
    for (f <- fields) {
      checkDependencies(f)
    }
    return Some(bareArgs.iterator ++ argIt)
  }

  def optionNotSupported(optionName: String) = {
    die ("Option " + optionName + " not supported. Try " +
	 progName + " --help.")
  }

  /** Parse one argument and return true if finished, i.e., if an argument
      cannot be parsed as an option. */
  def parseArg(s: String): ArgParse = {
    s match {
      case "-s" | "--show" => {
	showConfig
	return Ok
      }
      case "-v" | "--version" => {
	showVersion
	return Exit
      }
      case "-h" | "--help" => {
	usage
	return Exit
      }
      case s: String => {
	if (s.startsWith("--")) {
	  return parseLongOption(s.substring(2))
	} else if (s.startsWith("-") && s.length() > 1) {
	  // TODO: Check for "-no-x" for shorthands with negation
	  // examples: -x=y, -x
	  if (shorthands.contains(s.charAt(1)) &&
	      (s.length() == 2 || s.charAt(2) == '=')) {
	    return parseLongOption(shorthands(s.charAt(1)) +
				   s.substring(2), s.substring(1, 2))
	  }
	  optionNotSupported(s)
        }
	return BareArg
      }
    }
  }

  def cmdArgToFieldName(arg: String) = {
    val chars = arg.toCharArray()
    val name = new StringBuilder()
    var toUpper = false
    for (ch <- chars) {
      if (ch == '-') {
	toUpper = true
      } else if (toUpper) {
	if (!Character.isLowerCase(ch)) {
	  optionNotSupported("--" + arg)
	}
	name.append(Character.toUpperCase(ch))
	toUpper = false
      } else {
	name.append(ch)
      }
    }
    name.toString()
  }

  def parseLongOption(s: String, shorthand: String = ""): ArgParse = {
    if (s.isEmpty()) {
      return Done
    }
    var optionName: String = s
    val optionValIdx = s.indexOf('=')
    var optionValue: String = null
    var negBool = false
    if (optionValIdx != -1) {
      optionName = s.substring(0, optionValIdx)
      optionValue = s.substring(optionValIdx + 1)
    }

    if (s.startsWith("no-")) {
      optionName = optionName.substring(3)
      negBool = true
    }

    var argName = optionName
    if (shorthand != "") {
      argName = shorthand
    }

    try {
      val f =
	config.getClass().getDeclaredField(cmdArgToFieldName(optionName))
      f.setAccessible(true)

      // special case: setting boolean to false using --no-boolopt
      if (negBool) {
	return handleNegBool(f, argName, optionValue)
      }

      // other cases: primitive types and strings
      if (f.getType().isPrimitive()) {
	return parsePrimitiveType(f, argName, optionValue)
      }
      return parseString(f, argName, optionValue)
    } catch {
      case e: NoSuchFieldException => {
	optionNotSupported("--" + argName)
      }
    }
  }

  def handleNegBool (f: Field, optionName: String,
		     optionValue: String): ArgParse = {
    if (optionValue != null) {
      die("Arguments are not allowed for setting boolean option " +
	  optionName + " to false.")
    }
    if (isBoolean(f)) {
      setBoolean(f, config, false)
      return Ok
    } else {
      die("Argument --no-" + optionName +
	  " not allowed for non-boolean option.")
    }
  }

  def parsePrimitiveType (f: Field, optionName: String,
			  optionValue: String): ArgParse = {
    if (isBoolean(f)) {
      return parseBool(f, optionName, optionValue)
    }
    if ((optionValue == null) || (optionValue.isEmpty())) {
      die("Option " + optionName + " requires a value.")
    }
    val choice = f.getAnnotation(classOf[Choice])
    if (choice != null) {
      return setFieldAgainstChoice(f, optionName, optionValue, choice)
    }
    // pattern matching for f.getType() does not work due to type erasure
    if (isInt(f)) {
      return parseInt(f, optionName, optionValue)
    }
    if (isLong(f)) {
      return parseLong(f, optionName, optionValue)
    }
    if (isDouble(f)) {
      return parseDouble(f, optionName, optionValue)
    }
    die ("Parsing primitive types of type " + f.getType().getName() +
	 " is not implemented yet.")
  }

  def minViolated(value: String, optionName: String, min: String) = {
    "Value " + value + " for option " + optionName +
    " must be at least " + min + "."
  }

  def maxViolated(value: String, optionName: String, max: String) = {
    "Value " + value + " for option " + optionName +
    " must be at most " + max + "."
  }

  def checkRange(value: AnyVal, range: Range, optionName: String): Unit = {
    if (range != null) {
      (value: @unchecked) match {
	case i: Int => {
	  if (i < range.min()) {
	    die(minViolated(value.toString(), optionName,
		range.min().toString()))
	  }
	  if ((range.max() > range.min()) && (i > range.max())) {
// Added extra condition to work around problem when executing code under JPF
	    die(maxViolated(value.toString(), optionName,
		range.max().toString()))
	  }
	}
	case l: Long => {
	  var lVal = BigInteger.valueOf(l)
	  var maxVal = BigInteger.valueOf(range.ulmax)
	  if (l < 0) {
	    val TWO_COMPL_REF = BigInteger.ONE.shiftLeft(64)
	    lVal = lVal.add(TWO_COMPL_REF)
	    maxVal = maxVal.add(TWO_COMPL_REF)
	  }
	  if (lVal.compareTo(BigInteger.valueOf(range.ulmin)) == -1) {
	    die(minViolated(value.toString(), optionName,
		range.ulmin().toString()))
	  }
	  if (lVal.compareTo(maxVal) == 1) {
	    die(maxViolated(value.toString(), optionName,
		range.ulmax().toString()))
	  }
	}
	case d: Double => {
	  if (d < range.dmin()) {
	    die(minViolated(value.toString(), optionName,
		range.dmin().toString()))
	  }
	  if (d > range.dmax()) {
	    die(maxViolated(value.toString(), optionName,
		range.dmax().toString()))
	  }
	}
      }
    }
  }

  def getBase(f: Field) = {
    if (f.getAnnotation(classOf[Hex]) != null) {
      16
    } else {
      10
    }
  }

  def parseInt(f: Field, optionName: String,
	       optionValue: String): ArgParse = {
    try {
      val value = java.lang.Long.parseLong(optionValue, getBase(f))
      if (value > 0xffffffffL) {
	throw new NumberFormatException("Value overflow")
      }
      checkRange(value.toInt, f.getAnnotation(classOf[Range]), optionName)
      setInt(f, config, value.toInt)
      return Ok
    } catch {
      case e: NumberFormatException => {
	die("Illegal value " + optionValue + " for integer " +
	    optionName + ".")
      }
    }
  }

  def parseLong(f: Field, optionName: String,
		optionValue: String): ArgParse = {
    try {
      val value = new BigInteger(optionValue, getBase(f))
      if (value.compareTo(new BigInteger("ffffffffffffffff", 16)) > 0) {
	throw new NumberFormatException("Value overflow")
      }
      checkRange(value.longValue(),
		 f.getAnnotation(classOf[Range]), optionName)
      setLong(f, config, value.longValue())
      return Ok
    } catch {
      case e: NumberFormatException => {
	die("Illegal value " + optionValue + " for long " +
	    optionName + ".")
      }
    }
  }

  def parseDouble(f: Field, optionName: String,
		  optionValue: String): ArgParse = {
    try {
      val value = java.lang.Double.parseDouble(optionValue)
      checkRange(value, f.getAnnotation(classOf[Range]), optionName)
      setDouble(f, config, value)
      return Ok
    } catch {
      case e: NumberFormatException => {
	die("Illegal value " + optionValue + " for double " +
	    optionName + ".")
      }
    }
  }

  def parseBool(f: Field, optionName: String,
		optionValue: String): ArgParse = {
    optionValue match {
      case null => {
	setBoolean(f, config, true)
	return Ok
      }
      case "true" => {
	setBoolean(f, config, true)
	return Ok
      }
      case "false" => {
	setBoolean(f, config, false)
	return Ok
      }
      case s: String => {
	die("Illegal value " + optionValue +
	    " for boolean option " + optionName + ".")
      }
    }
  }

  def checkDependencies(f: Field): Unit = {
    val constraint = f.getAnnotation(classOf[Requires])
    if (constraint == null)
      return
    if (isBoolean(f) && !getBoolean(f, config))
      return
    if (isInt(f) && (getInt(f, config) == 0))
      return
    if (isLong(f) && (getLong(f, config) == 0L))
      return
    if (isDouble(f) && (getDouble(f, config) == 0.0))
      return
    if (get(f, config) == null)
      return

    val opt = constraint.opt()
    assert (opt != null)
    val otherField = config.getClass().getDeclaredField(opt)
    val current = get(otherField, config).toString

    val equals = constraint.equals()
    assert (equals != null)
    if (equals !=
	  classOf[Requires].getMethod("equals").getDefaultValue()) {
      val choice = getNameOfChoice(otherField, current)
      if (!equals.equals(choice)) {
	die("Usage of " + f.getName + " requires " + opt +
	    " (currently " + choice + ") to be set to " + equals + ".")
      }
      return
    }
    val notEquals = constraint.notEquals()
    assert (notEquals != null)
    assert (notEquals !=
	      classOf[Requires].getMethod("notEquals").getDefaultValue())
    val choice = getNameOfChoice(otherField, current)
    if (notEquals.equals(choice)) {
      die("Usage of " + f.getName + " requires " + opt +
	  " to be set to value other than " + notEquals + ".")
    }
  }

  def parseString(f: Field, optionName: String,
		  optionValue: String): ArgParse =  {
    val choice = f.getAnnotation(classOf[Choice])
    if (choice != null) {
      return setFieldAgainstChoice (f, optionName, optionValue, choice)
    }
    if (optionValue == null || optionValue.length() == 0) {
      die("Option string for " + optionName + " must be non-empty.")
    }
    set(f, config, optionValue)
    return Ok
  }

  def upperFirst(str: String) = {
    Character.toUpperCase(str.charAt(0)).toString() + str.substring(1)
  }

  def getConstValue(clsName: String, element: String) = {
    val cls = Class.forName(clsName).asInstanceOf[Class[Any]]
    val accessor = cls.getMethod(upperFirst(element))
    accessor.invoke(cls)
  }

  /* reverse lookup: get name of choice value */
  def getNameOfChoice(field: Field, value: String): String = {
    val range = field.getAnnotation(classOf[Choice])
    if (range == null)
      return value

    for (element <- range.value()) {
      val definingClass = range.definedIn()
      if (definingClass == "") {
	return value
      }
      val data = getConstValue(definingClass, element).toString
      if (value.equals(data)) {
	return element
      }
    }
    assert (false, { "Entry " + value + " not in range." })
    // should always be caught while parsing argument?
    return value
  }

  def setFieldAgainstChoice(field: Field, name: String, value: String,
			    range: Choice): ArgParse = {
    if (value == null) {
      die ("Option " + name + " needs an argument, which must be one of " +
	   formatArray(range.value()) + ".")
    }
    for (element <- range.value()) {
      if (value.equals(element)) {
	val definingClass = range.definedIn()
	if (definingClass != "") {
	  field.setInt(config,
		       getConstValue(definingClass, element).asInstanceOf[Int])
	  // TODO: When it becomes necessary, support other types
	  // (such as float)
	} else {
	  set(field, config, value)
	}
	return Ok
      }
    }
    die ("Invalid option for " + name + ": Must be one of " +
	 formatArray(range.value()) + ".")
  }

  def formatStr(name: String, l: Int) = {
    if (name.length() <= l) {
      (name + " " * l).substring(0, l)
    } else {
      name
    }
  }

  def valueOf(field: Field, instance: Object): String = {
    val value: Any = get(field, config)
    if (field.getAnnotation(classOf[Hex]) != null) {
      value match {
	case i: Int => Integer.toHexString(i)
	case l: Long => java.lang.Long.toHexString(l)
	case _ => value.toString() // ignore hex annotation here
      }
    } else value.toString()
  }

  def printField(field: Field): Unit = {
    val choice = field.getAnnotation(classOf[Choice])
    if (choice != null) {
      printFieldWithChoice (field, choice)
      return
    }
    val value = valueOf(field, config)
    val range = field.getAnnotation(classOf[Range])
    if (range != null) {
      if (isInt(field)) {
	printFieldWithBounds(field, value, range.min(), range.max())
      } else if (isLong(field)) {
	printFieldWithBounds(field, value, range.ulmin(), range.ulmax())
      } else if (isDouble(field)) {
	printFieldWithBounds(field, value, range.dmin(), range.dmax())
      }
    } else {
      printField(field,
		 formatStr(field.getName(), NameLength) + "\t" +
			   field.getType().getSimpleName() + "\t" +
		 formatStr(value, ValueLength))
    }
  }

  def maxStr(maxType: Class[_], maxValue: AnyVal) = {
    if ((isDouble(maxType) &&
	 (maxValue == Double.PositiveInfinity)) ||
	(isLong(maxType) &&
	 (maxValue == Long.MaxValue)) ||
	(isInt(maxType) &&
	 (maxValue == Int.MaxValue))) {
      "inf"
    } else {
      maxValue.toString
    }
  }

  def printFieldWithBounds(field: Field, value: String,
			   min: AnyVal, max: AnyVal): Unit = {
    printField(field,
	       formatStr(field.getName(), NameLength) +
	       "\t" + field.getType() + "\t" + formatStr(value, ValueLength) +
	       "\t" + min + ".." + maxStr(field.getType(), max))
  }

  /* Reflection magic to find declared name of constant corresponding
   * to a particular value, out of a range of possible constants. */
  def matchingConstant(value: AnyVal, range: Choice): String = {
    for (element <- range.value()) {
      if (getConstValue(range.definedIn(), element).equals(value)) {
	return element
      }
    }
    return null
  }

  def symbolicNameOfValue(value: Object, range: Choice): String = {
    if (value.isInstanceOf[String]) {
      value.asInstanceOf[String]
    } else {
      matchingConstant(value.asInstanceOf[AnyVal], range)
    }
  }

  def formatArray(range: Array[String]) = {
    val s: StringBuilder = new StringBuilder("{")
    var previous: Boolean = false
    for (element <- range) {
      if (previous) {
	s.append (", ")
      }
      s.append (element)
      previous = true
    }
    s.append("}")
  }

  def printField(field: Field, contents: String): Unit = {
    val out = new StringBuilder(contents)
    val constraint = field.getAnnotation(classOf[Requires])
    if (constraint != null) {
      val opt = constraint.opt()
      assert (opt != null)
      val equals = constraint.equals()
      assert (equals != null)
      if (equals !=
	    classOf[Requires].getMethod("equals").getDefaultValue()) {
	footnotes += Array("requires " + opt + " to be set to " +
			   constraint.equals() + " if option is set.")
      } else {
	val notEquals = constraint.notEquals()
	assert (notEquals != null)
	assert (notEquals !=
		  classOf[Requires].getMethod("notEquals").getDefaultValue())
	footnotes += Array("requires " + opt +
			   " to be set to value other than " +
			   constraint.notEquals() + " if option is set.")
      }
      out.append("\t[" + footnotes.length + "]")
    }
    println(out)
  }

  def printFieldWithChoice(field: Field, range: Choice): Unit = {
    printField(field,
	       formatStr(field.getName(), NameLength) + "\t" +
	       field.getType().getSimpleName() + "\t" +
	       formatStr(symbolicNameOfValue(get(field, config), range),
			 ValueLength) + "\t" + formatArray(range.value()))
  }

  def showConfig: ArgParse = {
    val fields: Array[Field] = config.getClass().getDeclaredFields()
    footnotes.clear()
    println(Console.BOLD +
	    formatStr("Option", NameLength) + "\tType   \t" +
	    formatStr("Value", ValueLength) + "\tRange" +
	    Console.RESET)
    for (f <- fields) {
      val fieldName = f.getName()
      assert (!fieldName.contains('$'))
      val fieldType: Class[_ <: Any] = f.getType()
      if (fieldType.isPrimitive() ||
	  (fieldType.isAssignableFrom(classOf[String]))) {
	printField(f)
      }
    }
    printFootnotes
  }

  def printFootnotes = {
    var i = 0
    for (f <- footnotes) {
      i += 1
      println("[" + i + "] " + f.mkString("\n    "))
    }
    Ok
  }
}
