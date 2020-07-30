package modbat.config

import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.PrintStream

import scala.io.Source
import scala.math.max

import org.scalatest._

object ConfigTest {
  def configTest(args: Array[String], splash: List[String]):
    (Iterator[String], Iterator[String]) = {
    val out: ByteArrayOutputStream = new ByteArrayOutputStream() 
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()

    Console.withErr(err) {
      Console.withOut(out) {
        val c = new ConfigMgr("ConfigTest", "[FILE]", new TestConfiguration(),
			  new Version ("modbat.config"), true)
        c.setSplashScreen(splash)
        ConfigMgr.printRemainingArgs(c.parseArgs(args))
      }
    }
    (scala.io.Source.fromString(out.toString).getLines(), scala.io.Source.fromString(err.toString).getLines())
  }

  def testCtor(args: Array[String]): (Iterator[String], Iterator[String]) = {
    configTest(args, List("This is a test", "for the splash screen"))
  }

  def testConfig(args: Array[String], errCode: Int = 0): Unit = {
    try {
      val logErr = configTest(args, List())
      if (errCode != 0) {
        assert (errCode == 0, "Error code " + Integer.toString(errCode) +
                              " expected but test was successful.")
      }
      checkOutput(args, logErr)
    } catch {
      case (e: Exception) =>
        assert(errCode != 0, "Caught unexpected exception: " + e.toString())
    }
  }

  def report(msg: String, lineNo: Int,
             context: Array[String], expected: String,
             actual: String): Unit = {
    val startLine = max(lineNo - 3, 1)
    System.err.println(msg)
    val endLine = lineNo - 1
    if (endLine > 0) {
      System.err.println("Context, lines " + Integer.toString(startLine) +
                         " - " + Integer.toString(endLine))
      for (l <- startLine to endLine) {
        System.err.println(Integer.toString(l) + ": " + context(l % 3))
      }
    }
    System.err.println(Integer.toString(lineNo) + "> " + actual)
    System.err.println(Integer.toString(lineNo) + "< " + expected)
  }

  def removeAnsiEscapes(line: String) = {
    line.replaceAll("\u009B|\u001B\\[[0-?]*[ -/]*[@-~]", "")
  }

  def sameAs[String](actual: Iterator[String], expected: Iterator[String],
    templateName: String): Boolean = {
    var l = 0
    val context = List("", "", "").toArray
    for (line <- expected) {
      val printableLine = removeAnsiEscapes(line.toString())
      l = l + 1
      if (!actual.hasNext) {
        report("Output truncated; matching context in template " +
               templateName + ":", l, context, printableLine, "")
        return false
      } else {
        val actLine = removeAnsiEscapes(actual.next().toString())
        if (printableLine.equals(actLine)) {
          context(l % 3) = printableLine
        } else {
          report("Output mismatch; matching context in template " +
                 templateName + ":", l, context, printableLine, actLine)
          return false
        }
      }
    }
    if (actual.hasNext) {
      report("Extra output; matching context in template " +
             templateName + ":", l + 1, context, "",
             removeAnsiEscapes(actual.next().toString()))
      return false
    }
    true
  }

  def logFileName(filename: String) = {
    if (filename.endsWith(".eout")) {
      filename.replace(".eout", ".err")
    } else {
      assert(filename.endsWith(".out"))
      filename.replace(".out", ".log")
    }
  }

  def checkFile(filename: String, output: Iterator[String]) = {
    val iters = output.duplicate
    val result = doCheck(filename, iters._1)
    if (!result) {
      val actualOutput = logFileName(filename)
      val writer = new BufferedWriter(new FileWriter(actualOutput))
      iters._2.map(l => removeAnsiEscapes(l) + "\n").foreach(writer.write)
      writer.close()
      System.err.println("diff " + filename.replace("../", "") +
                         " " + actualOutput.replace("../", ""))
    }
    assert(result, "Output does not match template")
  }

  def doCheck(filename: String, output: Iterator[String]) = {
    val logTemplFile = new File(filename)
    if (logTemplFile.exists()) {
      val logTemplate = Source.fromFile(logTemplFile).getLines
      sameAs(output, logTemplate, logTemplFile.getName())
    } else {
      val logTemplate = Iterator[String]()
      sameAs(output, logTemplate, logTemplFile.getName())
    }
  }

  def checkOutput(args: Array[String],
                  logErr: (Iterator[String], Iterator[String])) = {
    val logFileName = "../log/config/" + args.mkString("")
    checkFile(logFileName + ".out", logErr._1)
    checkFile(logFileName + ".eout", logErr._2)
  }
}

class ConfigTest extends FlatSpec with Matchers {
  "ConfigTest" should "run normally" in {
    val result = ConfigTest.testCtor(Array()) // no arguments
    result._1.toSeq should contain theSameElementsInOrderAs List("This is a test", "for the splash screen")
    result._2 shouldBe empty
  }

  "NoInput" should "produce no output" in ConfigTest.testConfig(Array())

  "showConfig" should "produce the same output as in the output template" in
    ConfigTest.testConfig(Array("-s"))

  "showConfigLong" should "produce the same output as in the output template" in
    ConfigTest.testConfig(Array("--show"))

  "IllegalArg" should "produce an exception" in
    ConfigTest.testConfig(Array("-x"), 1)

  "IllegalArg2" should "produce an exception" in
    ConfigTest.testConfig(Array("--x"), 1)

  "MultipleArguments" should "be parsed correctly" in
    ConfigTest.testConfig(Array("-s", "--mode=exec"))

  "DuplicateShow" should "show configuration each time" in
    ConfigTest.testConfig(Array("-s", "--mode=exec", "-s"))

  "Illegal option" should "show be recognized" in
    ConfigTest.testConfig(Array("-s", "--mode=quux", "-s"), 1)

  "No options" should "print remaining args to console" in
    ConfigTest.testConfig(Array("a", "b", "c"))

  "Double hyphen" should "print remaining args to console" in
    ConfigTest.testConfig(Array("--", "a", "b", "c"))

  "Double hyphen with options" should
    "print remaining option and args to console" in
    ConfigTest.testConfig(Array("--", "-h", "a", "b", "c"))

  "Double hyphen with long options" should
    "print remaining option and args to console" in
    ConfigTest.testConfig(Array("--", "--help", "a", "b", "c"))

  "Boolean flag syntax test 1" should "pass" in
    ConfigTest.testConfig(Array("--redirectOut", "-s"))

  "Boolean flag syntax test 2" should "pass" in
    ConfigTest.testConfig(Array("--redirectOut=true", "-s"))

  "Boolean flag syntax test 3" should "pass" in
    ConfigTest.testConfig(Array("--redirectOut=false", "-s"))

  "Boolean flag syntax test 4" should "fail" in
    ConfigTest.testConfig(Array("--redirectOut=xx", "-s"), 1)

  "Boolean flag dependency test 1" should "pass" in
    ConfigTest.testConfig(Array("--redirect-out", "--no-some-flag"))

  "Boolean flag dependency test 2" should "pass" in
    ConfigTest.testConfig(Array("--no-some-flag", "--redirect-out"))

  "Boolean flag dependency test 3" should "pass" in
    ConfigTest.testConfig(Array("--redirect-out", "--some-flag"))

  "Boolean flag dependency test 4" should "fail" in
    ConfigTest.testConfig(Array("--no-redirect-out", "--some-flag"), 1)

  "Dependency between boolean and numerical option" should "pass" in
    ConfigTest.testConfig(Array("--even-prime"))

  "Dependency between boolean and numerical option 2" should "pass" in
    ConfigTest.testConfig(Array("--no-even-prime"))

  "Dependency between boolean and numerical option 3" should "fail" in
    ConfigTest.testConfig(Array("--even-prime", "--small-prime=three"), 1)

  "Dependency between boolean and numerical option 4" should "fail" in
    ConfigTest.testConfig(Array("--odd-prime"), 1)

  "Dependency between boolean and numerical option 5" should "pass" in
    ConfigTest.testConfig(Array("--no-odd-prime"))

  "Dependency between boolean and numerical option 6" should "pass" in
    ConfigTest.testConfig(Array("--odd-prime", "--small-prime=three"))

  "Option syntax test 1" should "pass" in
    ConfigTest.testConfig(Array("--no-redirectOut", "-s"))

  "Option syntax test 2" should "fail" in
    ConfigTest.testConfig(Array("--no-redirectOut=true", "-s"), 1)

  "Option syntax test 3" should "fail" in
    ConfigTest.testConfig(Array("--no-redirectOut=false", "-s"), 1)

  "Option syntax test 4" should "fail" in
    ConfigTest.testConfig(Array("--no-redirectOut=xx", "-s"), 1)

  "Option syntax test 5" should "fail" in
    ConfigTest.testConfig(Array("--no-mode"), 1)

  "Option syntax test 6" should "fail" in
    ConfigTest.testConfig(Array("--nRuns"), 1)

  "Option syntax test 7" should "fail" in
    ConfigTest.testConfig(Array("--nRuns="), 1)

  "Option syntax test 8" should "fail" in
    ConfigTest.testConfig(Array("--nRuns=a"), 1)

  "Option syntax test 9" should "pass" in
    ConfigTest.testConfig(Array("--nRuns=1", "-s"))

  "Option syntax test 10" should "pass" in
    ConfigTest.testConfig(Array("--nRuns=999999", "-s"))

  "Option range test 1" should "fail" in
    ConfigTest.testConfig(Array("--nRuns=0"), 1)

  "Option range test 2" should "fail" in
    ConfigTest.testConfig(Array("--nRuns=999999999999"), 1)
}
