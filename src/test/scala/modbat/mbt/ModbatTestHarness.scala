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
import java.io.PrintWriter

import scala.util.matching.Regex
import scala.io.Source

import modbat.log.Log
import modbat.util.CloneableRandom

object ModbatTestHarness {
  def getNamesofFile(args: Array[String], td: org.scalatest.TestData): (String, String, String) = {
    // Be careful, this function create folder if doesn't exist
    var name_output = "log/modbat/"
    var arguments_name = ""
    for ( x <- args ) {
      if (x contains "modbat"){
        name_output = name_output + x
      }
      else{
        arguments_name = arguments_name + x
      }
    }
    var directory = new File(name_output);
    var bool = ! directory.exists()
    if (bool){
        directory.mkdirs();
    }
    (name_output, arguments_name, ( td.name.split(" ") )(0))
  }

  def addToFiles(writer_1: java.io.PrintWriter,writer_2: java.io.PrintWriter,data:String){
    writer_1.write(data+"\n")
    writer_2.write(data+"\n")  
  }
  
  def write(output_1: String, output_2: String, iterator_data: Iterator[String]): Unit = {
    val (writer_1, writer_2) = (new PrintWriter(new File(output_1)), new PrintWriter(new File(output_2)))
    while (iterator_data.hasNext) 
      addToFiles(writer_1, writer_2, iterator_data.next())
    writer_1.close()
    writer_2.close()
  }

  def savemv(from : String, to : String) {
    val src = new File(from)
    val dest = new File(to)
    new FileOutputStream(dest) getChannel() transferFrom(
    new FileInputStream(src) getChannel, 0, Long.MaxValue )
  }

  def compare_iterators(it1:Iterator[String], it2:Iterator[String]) : Boolean = {
    var current_iterator, current_iterator2="There is no element in the Iterator."
    var current_comparison = true
    while (current_comparison && (it1.hasNext || it2.hasNext)){
      ((it1.hasNext, it2.hasNext) match{
        case (true, true) => {
          current_iterator = it1.next
          current_iterator2 = it2.next
          if (current_iterator != current_iterator2){
            current_comparison=false
            current_iterator="iterators different : \n validated_out_lines = \n" + current_iterator + "\n out_filtered = \n" + current_iterator2
          }
        }
        case (false, false) => {
          current_comparison=false
        }
        case (false, true) => {
          current_comparison=false
          current_iterator="iterator 1 : end of file"
          current_iterator2 = it2.next
        }
        case (true, false) => {
          current_comparison=false
          current_iterator=it1.next
          current_iterator2="iterator 2 : end of file"
        }
      })
    }
    if (current_comparison==false){
      Console.println(current_iterator)
      Console.println(current_iterator2)
      current_comparison
    }
    current_comparison
  }

  def filtering_writing_comparing_each_file(name_output_1 : String, name_output_2 : String, typeByte : ByteArrayOutputStream){
    var regex_map = Map[String, String]()
    var replace_sentences = List("")
    if (name_output_1.split("\\.").last == "out"){
      regex_map = Map("""\[[0-9][0-9]*[mK]"""->"",""".*"""->"",""" in .*[0-9]* milliseconds"""->"","""RELEASE-([0-9.]*)"""->"$1",""" v[0-9a-f]* rev [0-9a-f]*"""->"",""" v[0-9][0-9]*\\.[0-9][^ ]* rev [0-9a-f]*"""->" vx.yz","""^Time: [0-9.]*"""->" vx.yz","""(at .*):[0-9]*:"""->"$1","""canceled 0, """->"","""(\[INFO\] .* java.lang.AssertionError.*):"""->"$1")
    }
    else if (name_output_1.split("\\.").last == "eout"){
      regex_map = Map ("""RELEASE-3.2"""->"3.3", """ v[0-9a-f]* rev [0-9a-f]*"""->" vx.yz", """ v[^ ]* rev [0-9a-f]*"""->" vx.yz", """(at .*?):[0-9]*:?"""->"$1", """(Exception in thread "Thread-)[0-9][0-9]*"""->"$1", """CommonRunner.*un.*\(ObjectRunner.scala"""->"", """MainGenericRunner.*un.*\(MainGenericRunner.scala"""->"")
    }
    else Console.println("Error : ModbatTestHarness - Unexpected end of file name") 
    val validated_out=name_output_1
    if (new java.io.File(validated_out).exists){
      val validated_out_lines = Source.fromFile(name_output_1).getLines
      val out_lines = Source.fromBytes(typeByte.toByteArray()).getLines
      val (filewhichcanbecompared,filewhichcanbecompared_copy) = out_lines.map (l => replaceRegexInSentence(l, regex_map)).duplicate
      write(name_output_1,name_output_2, filewhichcanbecompared)
      assert(compare_iterators(validated_out_lines, filewhichcanbecompared_copy))
    }
  }

  def filtering_writing_comparing(name_output: String, arguments_name: String, simple_name:String, out : ByteArrayOutputStream, err : ByteArrayOutputStream){
    filtering_writing_comparing_each_file (name_output+arguments_name+".out", name_output+simple_name+".out", out)
    filtering_writing_comparing_each_file (name_output+arguments_name+".eout", name_output+simple_name+".eout", err)
  }

  def replaceRegexInSentence(sentence : String, regex_map : Map[String,String]) : String = {  
    var sentence_filtred = sentence
    regex_map foreach (x => 
      sentence_filtred = sentence_filtred.replaceAll(x._1, x._2))
    sentence_filtred
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
          case e: Exception => {
            Modbat.ShutdownHandler.run
            ret=1
          }
        } finally {
          Main.config = origConfig
        }
      }
    }

    val (name_output, arguments_name, simple_name) = getNamesofFile(args, td)
    
    filtering_writing_comparing (name_output+"/", arguments_name, simple_name, out, err)
    
    optionsavemv match {
      case None => {}
      case Some((from,to)) => {
        savemv(from, to)
      }
    }

    (ret, scala.io.Source.fromString(out.toString).getLines().toList, scala.io.Source.fromString(err.toString).getLines().toList)
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

  def setJar(arg:String)={
    val mapsetTestJar = new java.util.HashMap[String, String]()
    mapsetTestJar.put("CLASSPATH", arg)
    setEnv (mapsetTestJar)
  }

  def setTestJar() = {
    setJar("build/modbat-test.jar")
  }

  def setExamplesJar() = {
    setJar("build/modbat-examples.jar")
  }

  def setFooJar() = {
    setJar("foo")
  }

}
