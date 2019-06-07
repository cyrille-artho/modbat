package modbat.mbt

import org.scalatest._

class JoinNull extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "JoinNull1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","modbat.test.JoinNull"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
