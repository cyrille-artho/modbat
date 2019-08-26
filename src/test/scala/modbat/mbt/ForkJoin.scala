package modbat.mbt

import org.scalatest._

class ForkJoin extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ForkJoin1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=30","--no-redirect-out","--log-level=fine","modbat.test.ForkJoin"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }

  "ForkJoin2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=3","--no-redirect-out","--log-level=fine","modbat.test.ForkJoin2"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
