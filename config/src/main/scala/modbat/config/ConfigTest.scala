package modbat.config

import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.math.BigInteger
import scala.math.Ordered
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

object ConfigTest {
  def main(args: Array[String]): Unit = {
    // parse arguments
    var c: ConfigMgr = null
    c = new ConfigMgr("ConfigTest", "[FILE]", new TestConfiguration(),
			new Version ("modbat.config"), true)
    c.setSplashScreen(List("This is a test", "for the splash screen"))
    c.parseArgs(args)
  }
}
