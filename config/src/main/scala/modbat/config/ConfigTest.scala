package modbat.config

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.Collections
import java.util.HashMap
import java.io.FileWriter

object ConfigTest {

  def using[A <: {def close(): Unit}, B](resource: A)(f: A => B): B = //TODO : remove redundancy with ModbatTestHarness 
  try f(resource) finally resource.close()

  def writeToFile(path: String, data: String): Unit = 
    using(new FileWriter(path))(_.write(data))

  def main(args: Array[String]): (Int, List[String], List[String]) = {
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    var ret = 0


    Console.withErr(err) {
      Console.withOut(out) {
        try {  
          var c: ConfigMgr = null
          c = new ConfigMgr("ConfigTest", "[FILE]", new TestConfiguration(),
            new Version ("modbat.config"), true)
          c.setSplashScreen(List("This is a test", "for the splash screen"))
          c.parseArgs(args)
          ret=0
        } catch {
          case e: Exception => ret=1
        } 
      }
    }
    var name_output = "log/modbat/" //TODO : Refactor code : Redundancy with ModbatTestHarness
    var name_file = ""
    for ( x <- args ) {
      if (x contains "modbat"){
        name_output = name_output + x
      }
      else{
        name_file = name_file + x
      }
    }
    name_output=name_output+"/"+name_file

    val name_output_err=name_output+".err.Alexandre"
    val name_output_out=name_output+".log.Alexandre"

    val err_value = err.toString
    val eout_value = out.toString

    writeToFile(name_output_err, err_value)
    writeToFile(name_output_out, eout_value)

    (ret, scala.io.Source.fromString(eout_value).getLines().toList, scala.io.Source.fromString(err_value).getLines().toList)
  }
}
