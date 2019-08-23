package modbat.mbt

import org.scalatest._

class SubModel extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "SubModel1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=debug","modbat.test.SubModel"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "SubModel2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--no-redirect-out","modbat.test.SubModel2"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }
}
