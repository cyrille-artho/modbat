package modbat.mbt

import org.scalatest._

class ChooseInBefore extends FlatSpec with Matchers {
  "ChooseInBefore1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=3","modbat.test.ChooseInBefore","--no-redirect-out"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}