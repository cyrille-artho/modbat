package modbat.mbt

import org.scalatest._

class TestObserver extends FlatSpec with Matchers {
  "TestObserver1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","--no-redirect-out","modbat.test.TestObserver"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}