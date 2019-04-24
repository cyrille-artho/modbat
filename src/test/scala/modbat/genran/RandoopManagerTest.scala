package modbat.genran

import modbat.mbt.Main
import org.scalatest.FunSuite

class RandoopManagerTest extends FunSuite {

  test("testRun") {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.examples.SimpleRandomModel", "--no-redirect-out"))

  }

}
