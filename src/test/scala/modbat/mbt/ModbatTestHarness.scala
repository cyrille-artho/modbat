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
  def getNamesofFile(args: Array[String], td: org.scalatest.TestData): (String, String) = {
    var name_folder = "log/modbat/"
    for ( x <- args ) {
      if (x contains "modbat."){
        name_folder = name_folder + x
      }
    }
    var directory = new File(name_folder);
    var bool = ! directory.exists()
    if (bool){
        directory.mkdirs();
    }
    (name_folder, ( td.name.split(" ") )(0))
  }
  
  //this function overwrite file (or creates it if it does not exist)
  def write(file: String, iterator_data: Iterator[String]): Unit = {
    val writer = new PrintWriter(new File(file))
    while (iterator_data.hasNext) 
      writer.println(iterator_data.next())
    writer.close()
  }

  def savemv(from : String, to : String) {
    val src = new File(from)
    val dest = new File(to)
    new FileOutputStream(dest) getChannel() transferFrom(
    new FileInputStream(src) getChannel, 0, Long.MaxValue )
  }

  def diffStr (templateFile: String) = {
    if (templateFile.endsWith(".eout")) {
      "diff " + templateFile.replace(".eout", ".err") + " " + templateFile
    } else {
      "diff " + templateFile.replace(".out", ".log") + " " + templateFile
    }
  }

  //this function compare 2 iterators => output boolean. 
  //If output is false, print the last match.
  def compareIterators(templateFile: String, it2:Iterator[String]) : Boolean = {
    val it1 = Source.fromFile(templateFile).getLines
    var past_iterator="End of file."
    var current_iterator=""
    var current_comparison = true
    while (current_comparison && (it1.hasNext || it2.hasNext)){
      ((it1.hasNext, it2.hasNext) match{
        case (true, true) => {
          current_iterator=it1.next()
          if (current_iterator != it2.next()){
            current_comparison=false
            Console.println(diffStr(templateFile))
            Console.println("Iterators are different.\nLast match :\n"+past_iterator)
          }
          else
            past_iterator=current_iterator
        }
        case _ => {
          current_comparison=false
          Console.println(diffStr(templateFile))
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
        """ v[0-9a-f]* rev [0-9a-f]*""" -> " vx.yz",
        """ v[0-9][0-9]*\\.[0-9][^ ]* rev [0-9a-f]*""" -> " vx.yz",
        """^Time: [0-9.]*""" -> "",
        """(at .*?):[0-9]*""" -> "$1",
        """canceled 0, """ -> ""
      )
    }

    else if (nameOutput.split("\\.").last == "err"){
      regex_map = Map (
        """RELEASE-3.2""" -> "3.3",
        """ v[0-9a-f]* rev [0-9a-f]*""" -> " vx.yz",
        """ v[^ ]* rev [0-9a-f]*""" -> " vx.yz",
        """(at .*?):[0-9]*""" -> "$1", 
        """(Exception in thread "Thread-)[0-9][0-9]*""" -> "$1X"
      )
      iterator_deleting_sentence = Iterator("""CommonRunner.*un.*\(ObjectRunner.scala""", """MainGenericRunner.*un.*\(MainGenericRunner.scala""")
    }

    else Console.println("Error : ModbatTestHarness - Unexpected end of file name") 
    
    val outLines = deleteMatchesSentenceInIterator ((Source.fromBytes(typeByte.toByteArray()).getLines),iterator_deleting_sentence)

    outLines.map (l => replaceRegexInSentence(l, regex_map))
  }

  def filterAndDuplicate(name_output: String, out : ByteArrayOutputStream, err : ByteArrayOutputStream):((Iterator[String], Iterator[String]), (Iterator[String], Iterator[String]))={
    (filtering (name_output+".log", out).duplicate,
    filtering (name_output+".err", err).duplicate)
  }

  def writeToFiles(name_folder: String, simple_name:String, filteredLog : Iterator[String], filteredErr : Iterator[String]){
    write(name_folder+simple_name+".log", filteredLog)
    write(name_folder+simple_name+".err", filteredErr)
  }

  def replaceRegexInSentence(sentence : String, regex_map : Map[String,String]) : String = {  
    var sentence_filtered = sentence
    regex_map foreach (x => 
      sentence_filtered = sentence_filtered.replaceAll(x._1, x._2))
    sentence_filtered.replace("\u001b","") //does it work ? Not sure...
  }

  def compareOutputWithTemplate(output : String, filteredLog : Iterator[String], filteredErr : Iterator[String]){
    if (new java.io.File(output+".out").exists){
      assert(compareIterators(output + ".out", filteredLog))
    } 
    else if (new java.io.File(output+".eout").exists){
      assert(compareIterators(output + ".eout", filteredErr))
    } 
  }

  def testMain(args: Array[String], env: () => Unit,
               td: org.scalatest.TestData,
               shouldFail: Boolean = false,
               optionsavemv: Option [(String, String)] = None):
               (Int, List[String], List[String]) = {
    env()
    val origOut = System.out
    val origErr = System.err
    val out: ByteArrayOutputStream = new ByteArrayOutputStream()
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()
    var ret = 0
    System.setOut(new PrintStream(out))
    System.setErr(new PrintStream(err))
    val config = new Configuration()
    val modbat = new Modbat(config)
    Console.withErr(err) {
      Console.withOut(out) {
        try {
          Main.run(modbat, args, config)
          ret=0
          System.setOut(origOut)
          System.setErr(origErr)
        } catch {
          case e: Exception => {
            modbat.ShutdownHandler.run
            System.setOut(origOut)
            System.setErr(origErr)
            // reset stderr/out before printing stack trace
            if (!shouldFail) {
              e.printStackTrace
            }
            ret=1
          }
        }
      }
    }

    val (name_folder, simple_name) = getNamesofFile(args, td)
    
    val ((filteredLog, filteredLogCopy), (filteredErr, filteredErrCopy)) = filterAndDuplicate (name_folder+"/"+simple_name, out, err)
    
    writeToFiles (name_folder+"/", simple_name, filteredLog, filteredErr)

    compareOutputWithTemplate (name_folder+"/"+simple_name, filteredLogCopy, filteredErrCopy)
     
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
