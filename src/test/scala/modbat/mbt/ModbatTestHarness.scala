package modbat.mbt

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.Collections
import java.util.HashMap

import modbat.config.ConfigTestHarness.bytesToLines
import modbat.config.ConfigTestHarness.checkFile
import modbat.config.ConfigTestHarness.{filter => configTestFilter}

object ModbatTestHarness {
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

  def runTest(args: Array[String], env: () => Unit, errCode: Int = 0): Unit = {
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    env()
    val config = new Configuration()
    val origOut = System.out
    val origErr = System.err
    System.setOut(new PrintStream(out))
    System.setErr(new PrintStream(err))
    Console.withErr(err) {
      Console.withOut(out) {
        try {
          Main.run(args, config)
          if (errCode != 0) {
            assert (errCode == 0, "Error code " + Integer.toString(errCode) +
                                  " expected but test was successful.")
          }
        } catch {
          case e: IllegalArgumentException => {
            System.setOut(origOut)
            System.setErr(origErr)
            checkOutput(args, bytesToLines(out), bytesToLines(err))
            throw e
          }
        }
      }
    }
    System.setOut(origOut)
    System.setErr(origErr)
    checkOutput(args, bytesToLines(out), bytesToLines(err))
  }

  def test(args: Array[String], env: () => Unit, errCode: Int = 0): Unit = {
    try {
      runTest(args, env, errCode)
    } catch {
      case (e: Exception) =>
        val cause = e.getCause()
        if (cause == null) {
          assert(errCode != 0, "Caught unexpected exception: " + e.toString())
        } else {
          assert(errCode != 0,
                 "Caught unexpected exception: " + e.toString() +
                 "; cause: " + e.getCause())
        }
    }
  }

  def filter(line: String) = {
    configTestFilter(line).
      replaceAll("(at .*):[0-9]*","$1")
  }

  def mainTarget(args: Array[String]) = {
    args.find(a => !a.startsWith("-")) match {
      case Some(s: String) => s
      case _ => ""
    }
  }

  def logFileName(args: Array[String]) = {
    val target = mainTarget(args)
    val params = args.filterNot(a => a.equals(target))
    target + "/" + params.mkString("")
  }

  def checkOutput(args: Array[String],
                  log: Iterator[String], err: Iterator[String]) = {
    val logFile = "log/modbat/" + logFileName(args)
    checkFile(logFile + ".out", log, filter)
    checkFile(logFile + ".eout", err, filter)
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

