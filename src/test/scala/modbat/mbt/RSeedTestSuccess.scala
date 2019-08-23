package modbat.mbt

import org.scalatest._

class RSeedTestSuccess extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "RSeedTestSuccess1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--no-redirect-out","modbat.test.RSeedTestSuccess"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }
}
