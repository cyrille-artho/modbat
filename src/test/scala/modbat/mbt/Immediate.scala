package modbat.mbt

import org.scalatest._

class Immediate extends FlatSpec with Matchers {
  "Immediate1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=100","--loop-limit=3","--no-redirect-out","--log-level=fine","modbat.test.Immediate"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}