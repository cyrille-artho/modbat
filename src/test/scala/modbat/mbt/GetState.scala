package modbat.mbt

import org.scalatest._

class GetState extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "GetState1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--log-level=fine","modbat.test.GetState","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }

  "GetState2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--log-level=fine","modbat.test.GetState2","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }
}
