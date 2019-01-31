package modbat.mbt

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream

object ModbatTestHarness {
  def testMain(args: Array[String]): (Int, List[String], List[String]) = {
    val out: ByteArrayOutputStream = new ByteArrayOutputStream() 
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    var ret = 0

    Console.withErr(err) {
      Console.withOut(out) {
        ret = Main.run(args)
      }
    }
    (ret, scala.io.Source.fromString(out.toString).getLines().toList, scala.io.Source.fromString(err.toString).getLines().toList)
  }
}

