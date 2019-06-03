package modbat.mbt

import org.scalatest._

class GenLabel extends FlatSpec with Matchers {
  "GenLabel1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--no-redirect-out","modbat.test.GenLabel"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "GenLabel2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--auto-labels","--no-redirect-out","modbat.test.GenLabel"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "GenLabel3" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--auto-labels","--no-redirect-out","modbat.test.GenLabel2"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "GenLabel4" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--auto-labels","--no-redirect-out","modbat.test.GenLabel3"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "GenLabel5" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--auto-labels","modbat.test.GenLabel3"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}