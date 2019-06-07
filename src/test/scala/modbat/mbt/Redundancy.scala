package modbat.mbt

import org.scalatest._

class Redundancy extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "Redundancy1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--no-redirect-out","--log-level=fine","modbat.test.Redundancy"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "Redundancy2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=2e78dcb10b272e5","-n=1","--no-redirect-out","modbat.test.Redundancy"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
