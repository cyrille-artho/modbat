package modbat.mbt

import org.scalatest._

class Choices extends FlatSpec with Matchers {
  "Choices1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--no-redirect-out","--show-choices","modbat.test.Choices"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "Choices2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--show-choices","modbat.test.Choices"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}