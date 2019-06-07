package modbat.mbt

import org.scalatest._

class ConcurrentModel extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ConcurrentModel1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","modbat.test.ConcurrentModel"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
