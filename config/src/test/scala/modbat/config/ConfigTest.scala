package modbat.config

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream

import org.scalatest._

object ConfigTest {
  def configTest(args: Array[String], splash: List[String]):
    (List[String], List[String]) = {
    val out: ByteArrayOutputStream = new ByteArrayOutputStream() 
    val err: ByteArrayOutputStream = new ByteArrayOutputStream()

    Console.withErr(err) {
      Console.withOut(out) {
        val c = new ConfigMgr("ConfigTest", "[FILE]", new TestConfiguration(),
			  new Version ("modbat.config"), true)
        c.setSplashScreen(splash)
        c.parseArgs(args)
      }
    }
    (scala.io.Source.fromString(out.toString).getLines().toList, scala.io.Source.fromString(err.toString).getLines().toList)
  }

  def testCtor(args: Array[String]): (List[String], List[String]) = {
    configTest(args, List("This is a test", "for the splash screen"))
  }

  def testConfig(args: Array[String]): (List[String], List[String]) = {
    configTest(args, List())
  }
}

class ConfigTest extends FlatSpec with Matchers {
  "ConfigTest" should "run normally" in {
    val result = ConfigTest.testCtor(Array()) // no arguments
    result._1 should contain theSameElementsInOrderAs List("This is a test", "for the splash screen")
    result._2 shouldBe empty
  }

  "NoInput" should "produce no output" in {
    val result = ConfigTest.testConfig(Array())
    result._1 shouldBe empty
    result._2 shouldBe empty
  }
}
