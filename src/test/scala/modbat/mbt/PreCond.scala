package modbat.mbt

import org.scalatest._

class PreCond extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "PreCond1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--no-redirect-out","modbat.test.PreCond"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }
}
