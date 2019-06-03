package modbat.mbt

import org.scalatest._

class WeightedTestWFrac extends FlatSpec with Matchers {
  "WeightedTestWFrac1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1000","--no-redirect-out","--abort-probability=0.1","--stop-on-failure","modbat.test.WeightedTestWFrac"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}