package modbat.config

import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.PrintStream
import java.io.PrintWriter

import scala.io.Source
import scala.math.max

object ConfigTestHarness {
  def writeToFiles(fileName: String,
                  filteredLog: Iterator[String],
                  filteredErr: Iterator[String]): Unit = {
    if (filteredLog.hasNext) {
      write(fileName + ".log", filteredLog)
    }
    if (filteredErr.hasNext) {
      write(fileName + ".err", filteredErr)
    }
  }

  //this function overwrite file (or creates it if it does not exist)
  def write(file: String, iterator_data: Iterator[String]): Unit = {
    val writer = new PrintWriter(new File(file))
    while (iterator_data.hasNext)
      writer.println(iterator_data.next())
    writer.close()
  }

  def testFileName(className: String, td: org.scalatest.TestData): String = {
    val dirName = className
    val directory = new File(dirName)
    if (! directory.exists()) {
System.err.println(directory.toString())
        directory.mkdirs()
    }
    val testName = td.name.substring(0, td.name.indexOf(td.text) - 1)
    val camelCaseFileName =
      " ([a-zA-Z0-9])".r.replaceAllIn(testName,
                                      { m => m.group(1).toUpperCase() })
    dirName + "/" + camelCaseFileName
  }

  def bytesToLines(bytes: ByteArrayOutputStream) =
    scala.io.Source.fromString(bytes.toString()).getLines()

  def runTest(className: String, args: Array[String],
              td: org.scalatest.TestData): Unit = {
    val shouldFail = td.text.startsWith("should fail")
    val out: ByteArrayOutputStream = new ByteArrayOutputStream() 
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    val logFileName = "../log/config/" + testFileName(className, td)
    val oldLogFileName = "../log/config/" + args.mkString("")
    var exc: Throwable = null

    Console.withErr(err) {
      Console.withOut(out) {
        val c = new ConfigMgr("ConfigMgr", "[FILE]", new TestConfiguration(),
                                new Version ("modbat.config"), true)
        try {
          ConfigMgr.printRemainingArgs(c.parseArgs(args))
          if (shouldFail) {
            assert (false,
                    "Non-zero error code expected but test was successful.")
          }
        } catch {
          case e: IllegalArgumentException => {
            Console.err.println(c.header)
            Console.err.println(e.getMessage())
          }
        }
      }
    }
    checkOutput(args, oldLogFileName, logFileName,
                bytesToLines(out), bytesToLines(err))
    if (exc != null) {
      throw exc
    }
  }

  def test(args: Array[String], td: org.scalatest.TestData)
    (implicit fullName: sourcecode.FullName): Unit = {
    val className =
      fullName.value.substring(0, fullName.value.lastIndexOf("."))
    val shouldFail = td.text.startsWith("should fail")
    try {
      runTest(className, args, td)
    } catch {
      case (e: Exception) =>
        assert(shouldFail, "Caught unexpected exception: " + e.toString())
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
    System.err.println(Integer.toString(lineNo) + "< " + expected)
    System.err.println(Integer.toString(lineNo) + "> " + actual)
  }

  def removeAnsiEscapes(line: String) = {
    line.replaceAll("\u009B|\u001B\\[[0-?]*[ -/]*[@-~]", "")
  }

  def filter(line: String) = {
    line.replaceAll(" v[^ ]* rev [^ ]*"," vx.yz")
  }

  def sameAs[String](actual: Iterator[String], expected: Iterator[String],
    templateName: String, filterFunc: String => String): Boolean = {
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
        val filteredLine = filterFunc(actLine.asInstanceOf[String])
        if (printableLine.equals(filteredLine)) {
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

  def checkFile(filename: String, output: Iterator[String],
                filterFunc: String => String = filter) = {
    val iters = output.duplicate
    val result = doCheck(filename, iters._1, filterFunc)
    if (!result) {
      val actualOutput = logFileName(filename)
      val writer = new BufferedWriter(new FileWriter(actualOutput))
      iters._2.map(l =>
                   filter(removeAnsiEscapes(l)) + "\n").foreach(writer.write)
      writer.close()
      System.err.println("diff " + actualOutput.replace("../", "") +
                         " " + filename.replace("../", ""))
    }
    result
  }

  def doCheck(filename: String, output: Iterator[String],
              filterFunc: String => String) = {
    val logTemplFile = new File(filename)
    if (logTemplFile.exists()) {
      val logTemplate = Source.fromFile(logTemplFile).getLines
      sameAs(output, logTemplate, logTemplFile.getName(), filterFunc)
    } else {
      val logTemplate = Iterator[String]()
      sameAs(output, logTemplate, logTemplFile.getName(), filterFunc)
    }
  }

  def checkOutput(args: Array[String], logFileName: String,
                  newLogFileName: String,
                  log: Iterator[String], err: Iterator[String]) = {
    val logIters = log.duplicate
    val errIters = err.duplicate
//System.err.println("git mv " + logFileName + " " + newLogFileName)
    writeToFiles (newLogFileName, logIters._1, errIters._1)
    val logMatch = checkFile(logFileName + ".out", logIters._2)
    val errMatch = checkFile(logFileName + ".eout", errIters._2)
    assert(logMatch, "Output does not match template")
    assert(errMatch, "Errors do not match template")
  }
}
