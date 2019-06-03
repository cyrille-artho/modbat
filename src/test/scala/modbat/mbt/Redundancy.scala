package modbat.mbt

import org.scalatest._

class Redundancy extends FlatSpec with Matchers {
  "Redundancy1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--no-redirect-out","--log-level=fine","modbat.test.Redundancy"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "Redundancy2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=2e78dcb10b272e5","-n=1","--no-redirect-out","modbat.test.Redundancy"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}