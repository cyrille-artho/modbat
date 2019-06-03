package modbat.mbt

import org.scalatest._

class EmptyModel extends FlatSpec with Matchers {
  "EmptyModel1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","--no-redirect-out","modbat.test.EmptyModel"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}