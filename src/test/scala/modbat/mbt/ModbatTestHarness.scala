package modbat.mbt

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.Collections
import java.util.HashMap
import java.io.FileWriter

object ModbatTestHarness {

  def using[A <: {def close(): Unit}, B](resource: A)(f: A => B): B =
  try f(resource) finally resource.close()

  def writeToFile(path: String, data: String): Unit = 
    using(new FileWriter(path))(_.write(data))

  def testMain(args: Array[String], env: () => Unit): (Int, List[String], List[String]) = {
    env()
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    var ret = 0

    Console.withErr(err) {
      Console.withOut(out) {
        try {
          Main.run(args)
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
    name_output=name_output+"/"+name_file

    val name_output_err=name_output+".err.Alexandre"
    val name_output_out=name_output+".log.Alexandre"

    val err_value = err.toString
    val eout_value = out.toString

    writeToFile(name_output_err, err_value)
    writeToFile(name_output_out, eout_value)

    (ret, scala.io.Source.fromString(eout_value).getLines().toList, scala.io.Source.fromString(err_value).getLines().toList)
  }
  
  def setEnv(newEnv: java.util.Map[String, String]) {
     try {
      val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
      val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
      theEnvironmentField.setAccessible(true)
      val env = theEnvironmentField.get(null).asInstanceOf[java.util.Map[String, String]]
      env.clear()
      env.putAll(newEnv)
      val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true)
      val cienv = new HashMap[String, String]()
      theCaseInsensitiveEnvironmentField.get(null)
      cienv.clear()
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

  def setTestJar() = {
    val mapsetTestJar = new java.util.HashMap[String, String]()
    mapsetTestJar.put("CLASSPATH", "build/modbat-test.jar")
    setEnv (mapsetTestJar)
  }

  def setExamplesJar() = {
    val mapsetTestJar = new java.util.HashMap[String, String]()
    mapsetTestJar.put("CLASSPATH", "build/modbat-examples.jar")
    setEnv (mapsetTestJar)
  }

  def setFooJar() = {
    val mapsetTestJar = new java.util.HashMap[String, String]()
    mapsetTestJar.put("CLASSPATH", "foo")
    setEnv (mapsetTestJar)
  }

}
