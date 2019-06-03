package modbat.mbt

import org.scalatest._

class Shortcut extends FlatSpec with Matchers {
  "Shortcut1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=3","--no-redirect-out","--log-level=fine","modbat.test.Shortcut"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "Shortcut2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.Shortcut"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}