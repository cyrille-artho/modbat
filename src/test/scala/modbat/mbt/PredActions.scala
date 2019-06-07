package modbat.mbt

import org.scalatest._

class PredActions extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "PredActions1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--loop-limit=50","modbat.test.PredActions"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
