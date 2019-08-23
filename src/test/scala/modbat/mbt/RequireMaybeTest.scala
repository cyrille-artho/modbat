package modbat.mbt

import org.scalatest._

class RequireMaybeTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "RequireMaybeTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.RequireMaybeTest","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "RequireMaybeTest2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.RequireMaybeTest2","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }
}
