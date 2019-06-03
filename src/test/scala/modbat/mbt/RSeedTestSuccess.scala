package modbat.mbt

import org.scalatest._

class RSeedTestSuccess extends FlatSpec with Matchers {
  "RSeedTestSuccess1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--no-redirect-out","modbat.test.RSeedTestSuccess"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}