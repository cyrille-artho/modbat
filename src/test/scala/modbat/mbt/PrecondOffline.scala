package modbat.mbt

import org.scalatest._

class PrecondOffline extends FlatSpec with Matchers {
  "PrecondOffline1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.PrecondOffline"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}