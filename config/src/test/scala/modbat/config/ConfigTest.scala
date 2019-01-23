package modbat.config

import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream

import org.scalatest._

object ConfigTest {
  def testCtor(args: Array[String]) {
    var out: ByteArrayOutputStream = new ByteArrayOutputStream() 
    var err: ByteArrayOutputStream = new ByteArrayOutputStream()

    Console.withErr(err) {
      Console.withOut(out) {
        val c = new ConfigMgr("ConfigTest", "[FILE]", new TestConfiguration(),
			  new Version ("modbat.config"), true)
        c.setSplashScreen(List("This is a test", "for the splash screen"))
        c.parseArgs(args)
      }
    }
  }
}

class ConfigTest extends FlatSpec with Matchers {
  "ConfigTest" should "run normally" in {
    ConfigTest.testCtor(Array()) // no arguments
  }
}

