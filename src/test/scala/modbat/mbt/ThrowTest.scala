package modbat.mbt

import org.scalatest._

class ThrowTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ThrowTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--loop-limit=5","modbat.test.ThrowTest"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }
}
