package modbat.mbt

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.Collections
import java.util.HashMap

import modbat.config.ConfigTestHarness.bytesToLines
import modbat.config.ConfigTestHarness.checkOutput
import modbat.config.ConfigTestHarness.{filter => configTestFilter}
import modbat.config.ConfigTestHarness.testFileName

object ModbatTestHarness {
  import Main.TestData

  def testMain(args: Array[String], env: () => Unit): (Int, List[String], List[String]) = {
    env()
    val config = new Configuration()
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()

    Console.withErr(err) {
      Console.withOut(out) {
        try {
          Main.run(args, config)
          (0, bytesToLines(out).toList, bytesToLines(err).toList)
        } catch {
          case _: Throwable =>
            (1, bytesToLines(out).toList, bytesToLines(err).toList)
        }
      }
    }
  }

  def runTest(className: String, args: Array[String], env: () => Unit,
              td: org.scalatest.TestData): Unit = {
    val shouldFail = td.text.startsWith("should fail")
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    env()
    val config = new Configuration()
    val origOut = System.out
    val origErr = System.err
    val testData = new TestData()
    val logFile = "log/modbat/" + getLogFileName(args)
    val logFileName = "log/modbat/" + testFileName(className, td)
    var exc: Throwable = null
    System.setOut(new PrintStream(out))
    System.setErr(new PrintStream(err))
    Console.withErr(err) {
      Console.withOut(out) {
        try {
          Main.run(args, config)
          if (shouldFail) {
            assert (false,
                    "Non-zero error code expected but test was successful.")
          }
        } catch {
          case e: Throwable => {
            testData.modbat.ShutdownHandler.run
            exc = e
          }
        }
      }
    }
    System.setOut(origOut)
    System.setErr(origErr)
    checkOutput(args, logFile, logFileName, bytesToLines(out), bytesToLines(err), filter)
    if (exc != null) {
      throw exc
    }
  }

  def test(args: Array[String], env: () => Unit,
           td: org.scalatest.TestData)
    (implicit fullName: sourcecode.FullName): Unit = {
    val className =
      fullName.value.substring(0, fullName.value.lastIndexOf("."))
    val shouldFail = td.text.startsWith("should fail")
    try {
      runTest(className, args, env, td)
    } catch {
      case (e: Throwable) =>
        val cause = e.getCause()
        if (cause == null) {
          assert(!shouldFail, "Caught unexpected exception: " + e.toString())
        } else {
          assert(!shouldFail,
                 "Caught unexpected exception: " + e.toString() +
                 "; cause: " + e.getCause())
        }
    }
  }

  def filter(line: String) = {
    configTestFilter(line).
      replaceAll("(at [^:]*):[0-9]*","$1")
  }

  def mainTarget(args: Array[String]) = {
    args.find(a => !a.startsWith("-")) match {
      case Some(s: String) => s
      case _ => ""
    }
  }

  def getLogFileName(args: Array[String]) = {
    val target = mainTarget(args)
    val params = args.filterNot(a => a.equals(target))
    target + "/" + params.mkString("")
  }

  def setEnv(newEnv: java.util.Map[String, String]): Unit = {
     try {
      val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
      val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
      theEnvironmentField.setAccessible(true)
      val env = theEnvironmentField.get(null).asInstanceOf[java.util.Map[String, String]]
      env.putAll(newEnv)
      val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true)
      val cienv = new HashMap[String, String]()
      theCaseInsensitiveEnvironmentField.get(null)
      cienv.putAll(newEnv)
    } catch {
      case e: NoSuchFieldException => {
        val classes = classOf[Collections].getDeclaredClasses()
        val env = System.getenv()
        for(cl <- classes) {
          if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
            val field = cl.getDeclaredField("m")
            field.setAccessible(true)
            val map = field.get(env).asInstanceOf[java.util.Map[String, String]]
            map.clear()
            map.putAll(newEnv)
          }
        }
      }
    }
  }

  def setExamplesJar() = {
    val mapsetExamplesJar = new java.util.HashMap[String, String]()
    mapsetExamplesJar.put("CLASSPATH", "build/modbat-examples.jar")
    setEnv (mapsetExamplesJar)
  }

  def setTestJar() = {
    val mapsetTestJar = new java.util.HashMap[String, String]()
    mapsetTestJar.put("CLASSPATH", "build/modbat-test.jar")
    setEnv (mapsetTestJar)
  }
}

