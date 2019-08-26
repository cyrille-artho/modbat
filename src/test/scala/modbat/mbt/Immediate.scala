package modbat.mbt

import org.scalatest._

class Immediate extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "Immediate1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=100","--loop-limit=3","--no-redirect-out","--log-level=debug","modbat.test.Immediate"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }
}
