package modbat.mbt

import org.scalatest._

class SingletonTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "SingletonTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.SingletonTest","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }
}
