package modbat.mbt

import org.scalatest._

class InvariantCheck extends FlatSpec with Matchers {
  "InvariantCheck1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--no-redirect-out","modbat.test.InvariantCheck"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}