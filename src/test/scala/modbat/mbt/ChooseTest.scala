package modbat.mbt

import org.scalatest._

class ChooseTest extends FlatSpec with Matchers {
  "ChooseTest1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=100","modbat.test.ChooseTest"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ChooseTest2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--show-choices","modbat.test.ChooseTest"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}