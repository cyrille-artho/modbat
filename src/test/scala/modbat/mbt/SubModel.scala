package modbat.mbt

import org.scalatest._

class SubModel extends FlatSpec with Matchers {
  "SubModel1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=debug","modbat.test.SubModel"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "SubModel2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--no-redirect-out","modbat.test.SubModel2"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "SubModel3" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--no-redirect-out","modbat.test.SubModel3"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}