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

  //  This function get the name and his path of the file. 
  //  For example, log/modbat/modbat.test.TickTockTest/TickTockTest1.
  //  This function creates a directory if it does not exist.
  def getNamesofFile(args: Array[String], td: org.scalatest.TestData): (String, String, String) = {
    var name_folder = "log/modbat/"
    var arguments_name = ""
    for ( x <- args ) {
      if (x contains "modbat."){
        name_folder = name_folder + x
      }
      else{
        arguments_name = arguments_name + x
      }
    }
    var directory = new File(name_folder);
    var bool = ! directory.exists()
    if (bool){
        directory.mkdirs();
    }
    (name_folder, arguments_name, ( td.name.split(" ") )(0))
  }

  //these functions add 1 line in 2 existing files. 
  def addToFiles(writer1: java.io.PrintWriter,writer2: java.io.PrintWriter,data:String){
    writer1.println(data)
    writer2.println(data)  
  }
  
  //this function overwrite the files file1 and file2 (or create it does not exist)
  def write(file1: String, file2: String, iterator_data: Iterator[String]): Unit = {
    val (writer1, writer2) = (new PrintWriter(new File(file1)), new PrintWriter(new File(file2)))
    while (iterator_data.hasNext) 
      addToFiles(writer1, writer2, iterator_data.next())
    writer1.close()
    writer2.close()
  }

  def savemv(from : String, to : String) {
    val src = new File(from)
    val dest = new File(to)
    new FileOutputStream(dest) getChannel() transferFrom(
    new FileInputStream(src) getChannel, 0, Long.MaxValue )
  }

  //this function compare 2 iterators => output boolean. 
  //If output is false, print the last match.
  def compareIterators(it1:Iterator[String], it2:Iterator[String]) : Boolean = {
    var past_iterator="End of file."
    var current_iterator=""
    var current_comparison = true
    while (current_comparison && (it1.hasNext || it2.hasNext)){
      ((it1.hasNext, it2.hasNext) match{
        case (true, true) => {
          current_iterator=it1.next()
          if (current_iterator != it2.next()){
            current_comparison=false
            Console.println("Iterators are different.\nLast match :\n"+past_iterator)
          }
          else
            past_iterator=current_iterator
        }
        case _ => {
          current_comparison=false
          Console.println("End of file.")
        }
      })
    }
    current_comparison
  }

  def deleteMatchesSentenceInIterator(it:Iterator[String], sentences_to_delete:Iterator[String]): Iterator[String]={
    var iterator_filtered = it
    var current_sentence_to_delete = ""
    while (sentences_to_delete.hasNext){
      current_sentence_to_delete = sentences_to_delete.next()
      iterator_filtered = iterator_filtered.dropWhile(l => l.contains(current_sentence_to_delete))
    }
      
    iterator_filtered
  }

  def filtering(nameOutput : String, typeByte : ByteArrayOutputStream):(Iterator[String])={
    var regex_map = Map[String, String]()
    var iterator_deleting_sentence = Iterator[String]()

    if (nameOutput.split("\\.").last == "log"){
      regex_map = Map(
        """\[[0-9][0-9]*[mK]""" -> "",
        """.*""" -> "",
        """ in .*[0-9]* milliseconds""" -> "",
        """RELEASE-([0-9.]*)""" -> "$1",
        """ v[0-9a-f]* rev [0-9a-f]*""" -> "",
        """ v[0-9][0-9]*\\.[0-9][^ ]* rev [0-9a-f]*""" -> " vx.yz",
        """^Time: [0-9.]*""" -> " vx.yz",
        """(at .*):[0-9]*:""" -> "$1",
        """canceled 0, """ -> "",
        """(\[INFO\] .* java.lang.AssertionError.*):"""->"$1"
      )
    }

    else if (nameOutput.split("\\.").last == "err"){
      regex_map = Map (
        """RELEASE-3.2""" -> "3.3",
        """ v[0-9a-f]* rev [0-9a-f]*""" -> " vx.yz",
        """ v[^ ]* rev [0-9a-f]*""" -> " vx.yz",
        """(at .*?):[0-9]*:?""" -> "$1", 
        """(Exception in thread "Thread-)[0-9][0-9]*""" -> "$1"
      )
      iterator_deleting_sentence = Iterator("""CommonRunner.*un.*\(ObjectRunner.scala""", """MainGenericRunner.*un.*\(MainGenericRunner.scala""")
    }

    else Console.println("Error : ModbatTestHarness - Unexpected end of file name") 
    
    val outLines = deleteMatchesSentenceInIterator ((Source.fromBytes(typeByte.toByteArray()).getLines),iterator_deleting_sentence)

    outLines.map (l => replaceRegexInSentence(l, regex_map))
  }

  def filtering2Files(name_output: String, out : ByteArrayOutputStream, err : ByteArrayOutputStream):((Iterator[String], Iterator[String]), (Iterator[String], Iterator[String]))={
    (filtering (name_output+".log", out).duplicate,
    filtering (name_output+".err", err).duplicate)
  }

  def writing2Files(name_folder: String, arguments_name: String, simple_name:String, filteredLog : Iterator[String], filteredErr : Iterator[String]){
    write(name_folder+arguments_name+".log",name_folder+simple_name+".log", filteredLog) 
    write(name_folder+arguments_name+".err",name_folder+simple_name+".err", filteredErr) 
  }

  def replaceRegexInSentence(sentence : String, regex_map : Map[String,String]) : String = {  
    var sentence_filtered = sentence
    regex_map foreach (x => 
      sentence_filtered = sentence_filtered.replaceAll(x._1, x._2))
    sentence_filtered.replace("\u001b","") //does it work ? Not sure...
  }

  def comparingFiles(output : String, filteredLog : Iterator[String], filteredErr : Iterator[String]){
    if (new java.io.File(output+".out").exists){
      val validated_out_lines = Source.fromFile(output+".out").getLines
      assert(compareIterators(validated_out_lines, filteredLog))
    } 
    else if (new java.io.File(output+".eout").exists){
      val validated_eout_lines = Source.fromFile(output+".eout").getLines
      assert(compareIterators(validated_eout_lines, filteredErr))
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
          case e: Exception => {
            e.printStackTrace
            Modbat.coverage
            ret=1
          }
        } finally {
          Main.config = origConfig
        }
      }
    }

    val (name_folder, arguments_name, simple_name) = getNamesofFile(args, td)
    
    val ((filteredLog, filteredLogCopy), (filteredErr, filteredErrCopy)) = filtering2Files (name_folder+"/"+arguments_name, out, err)
    
    writing2Files (name_folder+"/", arguments_name, simple_name, filteredLog, filteredErr)

    comparingFiles (name_folder+"/"+arguments_name,filteredLogCopy,filteredErrCopy)
     
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
