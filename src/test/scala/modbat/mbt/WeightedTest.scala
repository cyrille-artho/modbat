package modbat.mbt

import org.scalatest._

class WeightedTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "WeightedTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1000","--no-redirect-out","--abort-probability=0.1","--stop-on-failure","modbat.test.WeightedTest"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "WeightedTest2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--no-redirect-out","--log-level=debug","--stop-on-failure","modbat.test.WeightedTest2"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "WeightedTest3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1000","--no-redirect-out","--abort-probability=0.1","--stop-on-failure","modbat.test.WeightedTestWFrac"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }
}
