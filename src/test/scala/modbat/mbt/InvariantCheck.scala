package modbat.mbt

import org.scalatest._

class InvariantCheck extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "InvariantCheck1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--no-redirect-out","modbat.test.InvariantCheck"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
