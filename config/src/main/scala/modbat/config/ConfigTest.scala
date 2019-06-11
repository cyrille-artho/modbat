package modbat.config

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.Collections
import java.util.HashMap
import java.io.FileWriter
import java.io.File

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
    var name_output = "log/modbat/"
    var name_file = ""
    for ( x <- args ) {
      if (x contains "modbat"){
        name_output = name_output + x
      }
      else{
        name_file = name_file + x
      }
    }
    var directory = new File(name_output);
    var bool = ! directory.exists()
    if (bool){
        directory.mkdirs();
    }
    var name_output_1=name_output+"/"+name_file
    //var name_output_2=name_output+"/"+( td.name.split(" ") )(0)

    var name_output_err_1=name_output_1+".err"
    var name_output_out_1=name_output_1+".log"

    //var name_output_err_2=name_output_2+".err"
    //var name_output_out_2=name_output_2+".log"

    var err_value = err.toString
    var eout_value = out.toString
    
    writeToFile(name_output_err_1, err_value)
    writeToFile(name_output_out_1, eout_value)
    
    //writeToFile(name_output_err_2, err_value)
    //writeToFile(name_output_out_2, eout_value)
   
    (ret, scala.io.Source.fromString(eout_value).getLines().toList, scala.io.Source.fromString(err_value).getLines().toList)
  }
}
