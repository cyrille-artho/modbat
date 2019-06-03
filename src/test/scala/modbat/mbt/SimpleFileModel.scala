package modbat.mbt

import org.scalatest._

class SimpleFileModel extends FlatSpec with Matchers {
  "SimpleFileModel1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--no-redirect-out","modbat.test.SimpleFileModel"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}