package modbat.mbt

import org.scalatest._

class MaybeTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "MaybeTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.MaybeTest","--maybe-probability=0.0","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }

  "MaybeTest2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.MaybeTest","--maybe-probability=1.0","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "MaybeTest3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=100","modbat.test.MaybeTest","--maybe-probability=0.01","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }
}
