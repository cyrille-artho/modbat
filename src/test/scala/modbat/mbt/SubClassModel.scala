package modbat.mbt

import org.scalatest._

class SubClassModel extends FlatSpec with Matchers {
  "SubClassModel1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.SubClassModel"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}