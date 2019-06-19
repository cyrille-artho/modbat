package modbat.mbt

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.Collections
import java.util.HashMap
import java.io.FileWriter
import java.io.{File,FileInputStream}
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;

import scala.util.matching.Regex
import scala.io.Source

import modbat.log.Log
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

  def compare_iterators(it1:Iterator[String], it2:Iterator[String]) = {
    it1.zip(it2).forall(x => x._1 == x._2) && 
    (it1.length == it2.length)
  }

  def filtering_and_regex(name_output : String, typeoutput : String, typeByte : ByteArrayOutputStream){
    var regex_list = List("")
    var replace_sentences = List("")
    if (typeoutput == "out"){
      regex_list = List("""\[[0-9][0-9]*[mK]""", """.*""", """ in .*[0-9]* milliseconds""", """RELEASE-([0-9.]*)""",  """ v[0-9a-f]* rev [0-9a-f]*""", """ v[0-9][0-9]*\\.[0-9][^ ]* rev [0-9a-f]*""", """^Time: [0-9.]*""", """(at .*?):?[0-9]*:?""", """canceled 0, """)
      replace_sentences=List("", "", "", "$1", "", " vx.yz", " vx.yz", "$1", "")
    }
    else{
      regex_list = List("""RELEASE-3.2""", """ v[0-9a-f]* rev [0-9a-f]*""", """ v[^ ]* rev [0-9a-f]*""", """(at .*?):[0-9]*:?""", """(Exception in thread "Thread-)[0-9][0-9]*""", """CommonRunner.*un.*\(ObjectRunner.scala""", """MainGenericRunner.*un.*\(MainGenericRunner.scala""")
      replace_sentences = List("3.3", " vx.yz", " vx.yz", "$1", "$1", "", "")
    }
    val validated_out=name_output
    if (new java.io.File(validated_out).exists){
      val validated_out_lines = Source.fromFile(name_output).getLines
      val out_lines = Source.fromBytes(typeByte.toByteArray()).getLines
      val filewhichcanbecompared = (out_lines.map (l => replaceRegexInSentence(l, regex_list, replace_sentences)))
      Console.println("")
      Console.println(compare_iterators(validated_out_lines, filewhichcanbecompared))
    }
  }

  def replaceRegexInSentence(sentence : String, list_pattern : List[String], replace_sentences : List[String]) : String = {  
    if (list_pattern.length !=0){
      val pattern = list_pattern(0)
      val replace_sentence = replace_sentences(0)
      replaceRegexInSentence(
        sentence.replaceAll(pattern, replace_sentence), 
        list_pattern.drop(1), 
        replace_sentences.drop(1)
      )
    }
    else{
      sentence
    }
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
      
    val err_value = err.toString
    val eout_value = out.toString
  
    writeToFile(name_output_err_1, err_value)
    writeToFile(name_output_out_1, eout_value)
    
    writeToFile(name_output_err_2, err_value)
    writeToFile(name_output_out_2, eout_value)

    filtering_and_regex (name_output_1+".out", "out", out)
    /* filtering_and_regex (name_output_1+".eout", "err", err) */
    
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

