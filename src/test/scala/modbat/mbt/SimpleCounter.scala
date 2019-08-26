package modbat.mbt

import org.scalatest._

class SimpleCounter extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "SimpleCounter1" should "fail" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","--no-redirect-out","modbat.examples.SimpleCounter"), ModbatTestHarness.setExamplesJar, td)
    result._2 should not be empty
  }
}
