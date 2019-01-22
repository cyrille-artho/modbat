package modbat.config

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream

import org.scalatest._

object ConfigTest {
  def testCtor(args: Array[String]) {
    val baos = new ByteArrayOutputStream()
    System.setOut(new PrintStream(baos))

    val c = new ConfigMgr("ConfigTest", "[FILE]", new TestConfiguration(),
			  new Version ("modbat.config"), true)
    c.setSplashScreen(List("This is a test", "for the splash screen"))
    c.parseArgs(args)

    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)))
  }
}

class ConfigTest extends FlatSpec with Matchers {
  "ConfigTest" should "run normally" in {
    ConfigTest.testCtor(Array()) // no arguments
  }
}

