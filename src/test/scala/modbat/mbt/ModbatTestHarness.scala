package modbat.mbt

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.Collections
import java.util.HashMap
import java.io.FileWriter
import java.io.{File,FileInputStream}

import scala.util.matching.Regex
import scala.io.Source

import modbat.util.CloneableRandom

object ModbatTestHarness {

  def using[A <: {def close(): Unit}, B](resource: A)(f: A => B): B =
  try f(resource) finally resource.close()

  def writeToFile(path: String, data: String): Unit = 
    using(new FileWriter(path))(_.write(data))

  def savemv(from : String, to : String) {
    val src = new File(from)
    val dest = new File(to)
    new FileOutputStream(dest) getChannel() transferFrom(
    new FileInputStream(src) getChannel, 0, Long.MaxValue )
  }

  def replaceRegexInSentence(sentence : String, regex_list : List[Regex], replace_sentences : List[String]) : String = {
    var old_sentence = sentence
    var new_sentence=""
    var index=0
    for(index <- 0 to (regex_list.length-1)){
      regex_list.lift(index) match{
        case Some(regex) => 
        (
          replace_sentences.lift(index) match{
            case Some(replace_sentence) => 
            (
              new_sentence = regex.replaceAllIn(old_sentence, replace_sentence)
            )
            case None => println("Error.")
          }
        )
        case None => println("Error.")
      }
    }
    new_sentence
  }

  def testMain(args: Array[String], env: () => Unit, td: org.scalatest.TestData, optionsavemv : Option [(String, String)] = None): (Int, List[String], List[String]) = {
    env()
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    var ret = 0
    val origConfig = Main.config.clone.asInstanceOf[modbat.mbt.Configuration]

    Console.withErr(err) {
      Console.withOut(out) {
        try {
            Main.run(args) 
          ret=0
        } catch {
          case e: Exception => ret=1
        } finally {
          Main.config = origConfig
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
    val name_output_1=name_output+"/"+name_file
    val name_output_2=name_output+"/"+( td.name.split(" ") )(0)

    val name_output_err_1=name_output_1+".err"
    val name_output_out_1=name_output_1+".log"

    val name_output_err_2=name_output_2+".err"
    val name_output_out_2=name_output_2+".log"

    val validated_out=name_output_1+".out" // TODO: (only) if it exists, open and compare out vs file contents
    
    val validated_out_lines = Source.fromFile(name_output_1+".eout").getLines

    val err_value = err.toString
    val eout_value = out.toString
    
    writeToFile(name_output_err_1, err_value)
    writeToFile(name_output_out_1, eout_value)
    
    writeToFile(name_output_err_2, err_value)
    writeToFile(name_output_out_2, eout_value)

    val out_lines = out.getLines.toList

    val regex_out = List("""\[[0-9][0-9]*[mK]""".r, """.*//""".r, """ in .*[0-9]* milliseconds//""".r, """RELEASE-\([0-9.]*\)""".r,  """ v[0-9a-f]* rev [0-9a-f]*/""".r, """ v[0-9][0-9]*\\.[0-9][^ ]* rev [0-9a-f]*/ """.r, """^Time: [0-9.]*//""".r, """\\(at .*\\):[0-9]*""".r, """canceled 0, /""".r, """AIST confidential""".r)
    val string_replace_out = List("", "", "", "$1", "", " vx.yz/", " vx.yz/", "$1", "", "")
    val regex_eout = List("""RELEASE-3.2/3.3/""".r, """ v[0-9a-f]* rev [0-9a-f]*/""".r, """ v[^ ]* rev [0-9a-f]*/""".r, """\(at .*\):[0-9]*/""".r, """\(Exception in thread "Thread-\)[0-9][0-9]*/""".r, """CommonRunner.*.run.*(ObjectRunner.scala""".r, """MainGenericRunner.*.run.*(MainGenericRunner.scala""".r)
    val string_replace_eout = List("", " vx.yz/", " vx.yz/", "$1", "$1", "", "")

    val it = Iterator(validated_out_lines)
    for(out_line <- out_lines){
      sentence_out_after_filtering=replaceRegexInSentence(out_line, regex_out, string_replace_out)
      assert(it.hasNext(), "output is too long, longer than validated output")
      assert(sentence_out_after_filtering == replaceRegexInSentence(it.next(), regex_eout, string_replace_eout))
      assert(!it.hasNext(), "output is too short, shorter than validated output")  
    }

    
    optionsavemv match {
      case None => {}
      case Some((from,to)) => {
        savemv(from, to)
      }
    }

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

