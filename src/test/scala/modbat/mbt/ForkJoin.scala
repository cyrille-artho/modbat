package modbat.mbt

import org.scalatest._

class ForkJoin extends FlatSpec with Matchers {
  "ForkJoin1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=30","--no-redirect-out","--log-level=fine","modbat.test.ForkJoin"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ForkJoin2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=3","--no-redirect-out","--log-level=fine","modbat.test.ForkJoin2"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}