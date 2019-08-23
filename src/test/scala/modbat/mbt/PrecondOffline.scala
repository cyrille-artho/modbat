package modbat.mbt

import org.scalatest._

class PrecondOffline extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "PrecondOffline1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.PrecondOffline"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }
}
