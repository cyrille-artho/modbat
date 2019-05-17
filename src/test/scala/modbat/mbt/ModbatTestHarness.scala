package modbat.mbt

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream

object ModbatTestHarness {
  def testMain(args: Array[String], env: () => Unit): (Int, List[String], List[String]) = {
    // TODO (issue #27): refactor result into a class returning Option[Exception], List[String]^2
    env()
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    var ret = 0

    Console.withErr(err) {
      Console.withOut(out) {
        ret = Main.run(args)
//        try {
//          run(args)
//          ret=0
//        } catch {
//          case e: Exception => ret=1
//        }
      }
    }
    (ret, scala.io.Source.fromString(out.toString).getLines().toList, scala.io.Source.fromString(err.toString).getLines().toList)
  }

  def setTestJar() = {
    System.setProperty("CLASSPATH", "build/modbat-test.jar")
  }
}

